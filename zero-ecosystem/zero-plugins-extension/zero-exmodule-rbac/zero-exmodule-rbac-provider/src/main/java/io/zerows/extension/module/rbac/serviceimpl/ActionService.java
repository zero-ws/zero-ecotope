package io.zerows.extension.module.rbac.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.management.uri.UriAeon;
import io.zerows.cortex.management.uri.UriMeta;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.SActionDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SResourceDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.module.rbac.domain.tables.pojos.SResource;
import io.zerows.extension.module.rbac.servicespec.ActionStub;
import io.zerows.extension.skeleton.spi.ScRoutine;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class ActionService implements ActionStub {

    @Override
    public Future<SAction> fetchAction(final String normalizedUri,
                                       final HttpMethod method) {
        return this.fetchAction(normalizedUri, method, null);
    }

    @Override
    public Future<List<SAction>> fetchAction(final String permissionId) {
        return DB.on(SActionDao.class).fetchAsync(KName.PERMISSION_ID, permissionId);
    }

    @Override
    public Future<SAction> fetchAction(final String normalizedUri,
                                       final HttpMethod method,
                                       final String sigma) {
        final JsonObject actionFilters = new JsonObject();
        actionFilters.put(VString.EMPTY, Boolean.TRUE);
        actionFilters.put(KName.URI, normalizedUri);
        if (Ut.isNotNil(sigma)) {
            actionFilters.put(KName.SIGMA, sigma);
        }
        actionFilters.put(KName.METHOD, method.name());
        return DB.on(SActionDao.class)
            .fetchOneAsync(actionFilters);
    }

    @Override
    public Future<SResource> fetchResource(final String key) {
        return DB.on(SResourceDao.class)
            .fetchByIdAsync(key);
    }

    @Override
    public Future<List<SAction>> searchAuthorized(final String keyword, final String sigma) {
        if (Ut.isNil(sigma) || Ut.isNil(keyword)) {
            return Ux.future(new ArrayList<>());
        } else {
            /*
             * Build condition for spec situations
             *
             * 1. The method must be filtered ( Valid for GET / POST )
             * 2. The records must belong to application with the same `sigma`
             */
            final JsonObject condition = new JsonObject();
            condition.put(KName.SIGMA, sigma);
            final JsonArray methods = new JsonArray();
            methods.add(HttpMethod.POST.name());
            methods.add(HttpMethod.GET.name());
            condition.put(KName.METHOD, methods);
            /*
             * 3. keyword processing
             */
            final JsonObject criteria = new JsonObject();
            criteria.put(KName.NAME + ",c", keyword);
            criteria.put(KName.CODE + ",c", keyword);
            criteria.put(KName.URI + ",c", keyword);
            condition.put("$0", criteria);
            return DB.on(SActionDao.class).fetchAndAsync(condition);
        }
    }

    @Override
    public Future<List<UriMeta>> searchAll(final String keyword, final String sigma) {
        /*
         * Static by `keyword` of UriMeta
         */
        final List<UriMeta> staticList = UriAeon.uriSearch(keyword);
        /*
         * Dynamic by `keyword` and `sigma` ( zero-jet )
         */
        return HPI.of(ScRoutine.class).waitAsync(
            route -> route.searchAsync(keyword, sigma),
            ArrayList::new
        ).compose(uris -> {
            /*
             * Combine two list of uri metadata
             */
            final List<UriMeta> resultList = new ArrayList<>(uris);
            resultList.addAll(staticList);
            /*
             * After combine, re-order the result list by `uri`
             */
            resultList.sort(Comparator.comparing(UriMeta::getUri));
            return Ux.future(resultList);
        });
    }

    @Override
    public Future<List<SAction>> saveAction(final SPermission permission, final JsonArray actionData) {
        /*
         * Read action list of original
         */
        return DB.on(SActionDao.class).<SAction>fetchAsync(KName.PERMISSION_ID, permission.getId())
            .compose(oldList -> {
                /*
                 * Get actions of input
                 */
                final List<SAction> inputList = Ux.fromJson(actionData, SAction.class);

                final ConcurrentMap<String, SAction> mapInput = Ut.elementMap(inputList, SAction::getId);
                final ConcurrentMap<String, SAction> mapStored = Ut.elementMap(oldList, SAction::getId);
                /*
                 * Remove link
                 */
                final List<SAction> updated = new ArrayList<>();
                oldList.forEach(original -> {
                    /*
                     * Existing in inputMap but not in original
                     * Here should remove link between Permission / Action
                     */
                    if (!mapInput.containsKey(original.getId())) {
                        this.setAction(original, permission, null);
                        updated.add(original);
                    }
                });
                /*
                 * Add link
                 */
                mapInput.keySet().stream().filter(key -> !mapStored.containsKey(key)).forEach(actionKey -> {
                    final SAction action = mapInput.get(actionKey);
                    this.setAction(action, permission, permission.getId());
                    updated.add(action);
                });
                return DB.on(SActionDao.class).updateAsync(updated);
            });
    }

    private void setAction(final SAction action, final SPermission permission, final String permissionId) {
        action.setPermissionId(permissionId);
        action.setUpdatedAt(LocalDateTime.now());
        action.setUpdatedBy(permission.getUpdatedBy());
        action.setActive(Boolean.TRUE);
        action.setLanguage(permission.getLanguage());
        action.setSigma(permission.getSigma());
    }

    @Override
    public Future<Boolean> removeAction(final String permissionId, final String userKey) {
        return DB.on(SActionDao.class).<SAction>fetchAsync(KName.PERMISSION_ID, permissionId)
            .compose(actions -> {
                /*
                 * actions modification, no createdBy processing here
                 */
                actions.forEach(action -> {
                    action.setPermissionId(null);
                    action.setUpdatedAt(LocalDateTime.now());
                    action.setUpdatedBy(userKey);
                });
                return DB.on(SActionDao.class).updateAsync(actions);
            })
            .compose(nil -> Ux.future(Boolean.TRUE));
    }
}
