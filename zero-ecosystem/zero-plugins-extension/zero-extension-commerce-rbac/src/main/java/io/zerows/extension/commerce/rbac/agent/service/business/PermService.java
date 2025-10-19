package io.zerows.extension.commerce.rbac.agent.service.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.commerce.rbac.agent.service.accredit.ActionStub;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SActionDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SPermSetDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SPermissionDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.RRolePerm;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPermSet;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.commerce.rbac.uca.logged.ScRole;
import io.zerows.platform.constant.VName;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import io.zerows.support.base.FnBase;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class PermService implements PermStub {
    @Inject
    private transient ActionStub actionStub;

    @Override
    public Future<JsonObject> syncAsync(final JsonArray removed, final JsonObject relation,
                                        final String userKey) {
        /*
         * Removed Permission Id from S_ACTION
         * Update all the action permissionId = null by key
         */
        final List<Future<SAction>> entities = new ArrayList<>();
        final DBJooq jooq = DB.on(SActionDao.class);
        Ut.itJString(removed).map(key -> jooq.<SAction>fetchByIdAsync(key)

            /*
             * Set all queried permissionId of each action to null
             * Here should remove permissionId to set resource to freedom
             */
            .compose(action -> {

                /*
                 * Remove relation between
                 * Action / Permission
                 */
                action.setPermissionId(null);
                action.setUpdatedBy(userKey);
                action.setUpdatedAt(LocalDateTime.now());
                return Ux.future(action);
            })
            .compose(jooq::updateAsync)
        ).forEach(entities::add);
        return FnBase.combineT(entities).compose(actions -> {

            /*
             * Build relation between actionId -> permissionId
             */
            final List<Future<SAction>> actionList = new ArrayList<>();
            Ut.<String>itJObject(relation, (permissionId, actionId) -> actionList.add(
                jooq.<SAction>fetchByIdAsync(actionId).compose(action -> {

                    /*
                     * Update relation between
                     * Action / Permission
                     */
                    action.setPermissionId(permissionId);
                    action.setUpdatedBy(userKey);
                    action.setUpdatedAt(LocalDateTime.now());
                    return Ux.future(action);
                }).compose(jooq::updateAsync)
            ));
            return FnBase.combineT(actionList);
        }).compose(nil -> Ux.future(relation));
    }

    @Override
    public Future<JsonArray> syncAsync(final JsonArray permissions, final String roleId) {
        final JsonObject condition = new JsonObject();
        condition.put(KName.Rbac.ROLE_ID, roleId);
        /*
         * Delete all the relations that belong to roleId
         * that the user provided here
         * */
        final DBJooq dao = DB.on(RRolePermDao.class);
        return dao.deleteByAsync(condition).compose(processed -> {
            /*
             * Build new relations that belong to the role
             */
            final List<RRolePerm> relations = new ArrayList<>();
            Ut.itJArray(permissions, String.class, (permissionId, index) -> {
                final RRolePerm item = new RRolePerm();
                item.setRoleId(roleId);
                item.setPermId(permissionId);
                relations.add(item);
            });
            return dao.insertAsync(relations).compose(inserted -> {
                /*
                 * Refresh cache pool with Sc interface directly
                 */
                final ScRole role = ScRole.login(roleId);
                return role.refresh(permissions).compose(nil -> Ux.future(inserted));
            }).compose(Ux::futureA);
        });
    }

    @Override
    public Future<JsonObject> searchAsync(final JsonObject query, final String sigma) {
        /*
         * Result for searching join S_PERMISSIONS
         */
        return DB.on(SPermSetDao.class).<SPermSet>fetchAsync(KName.SIGMA, sigma).compose(setList -> {
            /*
             * Extract perm codes to set
             */
            final Set<String> codes = setList.stream().map(SPermSet::getCode).collect(Collectors.toSet());

            /*
             * Search permissions that related current
             */
            final JsonObject criteriaRef = query.getJsonObject(VName.KEY_CRITERIA);
            /*
             * Combine condition here
             */
            final JsonObject criteria = new JsonObject();
            criteria.put(KName.SIGMA, sigma);
            criteria.put("code,!i", Ut.toJArray(codes));
            criteria.put(VString.EMPTY, Boolean.TRUE);
            if (Ut.isNotNil(criteriaRef)) {
                criteria.put("$0", criteriaRef.copy());
            }
            /*
             * criteria ->
             * SIGMA = ??? AND CODE NOT IN (???)
             * */
            query.put(VName.KEY_CRITERIA, criteria);

            /*
             * Replace for criteria
             */
            return DB.on(SPermissionDao.class).searchAsync(query);
        });
    }

    @Override
    public Future<JsonObject> fetchAsync(final String key) {

        /* Read permission and actions */
        return DB.on(SPermissionDao.class).<SPermission>fetchByIdAsync(key)

            /* Secondary Fetching, Fetch action here */
            .compose(permission -> this.actionStub.fetchAction(permission.getKey())

                /* futureJM to combine two result to JsonObject */
                .compose(Ux.futureJM(permission, KName.ACTIONS))
            );
    }

    @Override
    public Future<JsonObject> createAsync(final JsonObject body) {
        final JsonArray actions = body.getJsonArray(KName.ACTIONS);
        body.remove(KName.ACTIONS);
        return DB.on(SPermissionDao.class).<SPermission>insertAsync(body)

            /* Synced Action */
            .compose(permission -> this.actionStub.saveAction(permission, actions)

                /* futureJM to combine two result to JsonObject */
                .compose(Ux.futureJM(permission, KName.ACTIONS))
            );
    }

    @Override
    public Future<JsonObject> updateAsync(final String key, final JsonObject body) {
        final JsonArray actions = body.getJsonArray(KName.ACTIONS);
        body.remove(KName.ACTIONS);
        return DB.on(SPermissionDao.class).<SPermission, String>updateAsync(key, body)

            /* Synced Action */
            .compose(permission -> this.actionStub.saveAction(permission, actions)

                /* futureJM to combine two result to JsonObject */
                .compose(Ux.futureJM(permission, KName.ACTIONS))
            );
    }

    @Override
    public Future<Boolean> deleteAsync(final String key, final String userKey) {
        return DB.on(SPermissionDao.class).deleteByIdAsync(key)
            .compose(nil -> this.actionStub.removeAction(key, userKey));
    }
}
