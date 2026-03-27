package io.zerows.extension.module.rbac.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SActionDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SPermissionDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RRolePerm;
import io.zerows.extension.module.rbac.domain.tables.pojos.SAction;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermission;
import io.zerows.extension.module.rbac.metadata.logged.ScRole;
import io.zerows.extension.module.rbac.servicespec.ActionStub;
import io.zerows.extension.module.rbac.servicespec.PermStub;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        final ADB jooq = DB.on(SActionDao.class);
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
        return Fx.combineT(entities).compose(actions -> {

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
            return Fx.combineT(actionList);
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
        final ADB dao = DB.on(RRolePermDao.class);
        return dao.deleteByAsync(condition).compose(processed -> {
            /*
             * Build new relations that belong to the role
             */
            final List<RRolePerm> relations = new ArrayList<>();
            Ut.itJArray(permissions).forEach(permission -> {
                final String permissionId = permission.getString(KName.Rbac.PERM_ID);
                if (Ut.isNotNil(permissionId)) {
                    final RRolePerm item = new RRolePerm();
                    item.setRoleId(roleId);
                    item.setPermId(permissionId);
                    relations.add(item);
                }
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
        return Ux.futureJ();
    }

    @Override
    public Future<JsonObject> fetchAsync(final String key) {

        /* Read permission and actions */
        return DB.on(SPermissionDao.class).<SPermission>fetchByIdAsync(key)

            /* Secondary Fetching, Fetch action here */
            .compose(permission -> this.actionStub.fetchAction(permission.getId())

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
