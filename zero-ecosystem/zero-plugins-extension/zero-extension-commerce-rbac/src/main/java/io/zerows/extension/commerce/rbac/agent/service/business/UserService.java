package io.zerows.extension.commerce.rbac.agent.service.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.UObject;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.commerce.rbac.domain.tables.daos.OUserDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RUserGroupDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.uca.acl.relation.Junc;
import io.zerows.extension.commerce.rbac.util.Sc;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;

public class UserService implements UserStub {

    // ================== Type Part for S_USER usage ================

    /*
     * Async for user information
     * 1) Fetch information from S_USER
     * 2) Re-calculate the information by `modelId/modelKey` instead of ...
     *    -- Employee: modelId = employee
     *    -- Member:   modelId = member
     * 3) Fetch secondary information based join configuration by
     *    key = modelKey
     *
     * The whole level should be
     *
     */
    @Override
    public Future<JsonObject> fetchInformation(final String userId) {
        return DB.on(SUserDao.class)
            /* User Information */
            .<SUser>fetchByIdAsync(userId)
            /* Employee Information */
            .compose(Junc.refExtension()::identAsync)
            /* Relation for roles / groups */
            .compose(Junc.refRights()::identAsync)
            /* Setting for user */
            .compose(Junc.refSetting()::identAsync);
    }

    // ================== Basic Part of S_User ================

    @Override
    public Future<JsonObject> fetchAuthorized(final SUser query) {
        return DB.on(OUserDao.class).fetchOneAsync(AuthKey.F_CLIENT_ID, query.getKey())
            .compose(Ux::futureJ)
            .compose(ouserJson -> {
                final JsonObject userJson = Ut.serializeJson(query);
                final JsonObject merged = Ut.valueAppend(userJson, ouserJson);
                return UObject.create(merged).pickup(
                    KName.KEY,                /* client_id parameter */
                    AuthKey.SCOPE,              /* scope parameter */
                    AuthKey.STATE,              /* state parameter */
                    AuthKey.F_CLIENT_SECRET,    /* client_secret parameter */
                    AuthKey.F_GRANT_TYPE        /* grant_type parameter */
                ).denull().toFuture();
            }).compose(response -> {
                final String initPwd = Sc.valuePassword();
                if (initPwd.equals(query.getPassword())) {
                    /* Password Init */
                    response.put(KName.PASSWORD, false);
                }
                return Ux.future(response);
            });
    }

    /*
     * 只附加更新关联对象，该API不更新和账号本身相关的内容如
     * -- Role
     * -- Group
     * -- OUser
     */
    @Override
    public Future<JsonObject> updateInformation(final String userId, final JsonObject params) {
        final SUser user = Ux.fromJson(params, SUser.class);
        user.setKey(userId);
        return DB.on(SUserDao.class).updateAsync(userId, user)
            .compose(userInfo -> Junc.refExtension().identAsync(userInfo, params));
    }

    @Override
    public Future<JsonObject> createUser(final JsonObject params) {
        final SUser user = Ux.fromJson(params, SUser.class);
        /*
         * 创建账号时如果没有密码则设置初始密码
         * 初始密码配置位置：plugin/rbac/configuration.json
         */
        if (Objects.isNull(user.getPassword())) {
            user.setPassword(Sc.valuePassword());
        }
        final KRef refer = new KRef();
        return DB.on(SUserDao.class).insertAsync(user)
            .compose(refer::future)
            // 创建认证信息
            .compose(inserted -> Sc.valueAuth(inserted, params))
            // Insert new OUser Record
            .compose(oUser -> DB.on(OUserDao.class).insertAsync(oUser))
            // delete attribute: password from user information To avoid update to EMPTY string
            .compose(entity -> Ux.futureJ(refer.<SUser>get().setPassword(null)));
    }

    @Override
    public Future<Boolean> deleteUser(final String userKey) {
        final ADB sUserDao = DB.on(SUserDao.class);
        final ADB oUserDao = DB.on(OUserDao.class);
        final ADB rUserRoleDao = DB.on(RUserRoleDao.class);
        final ADB rUserGroupDao = DB.on(RUserGroupDao.class);

        return oUserDao.fetchOneAsync(new JsonObject().put(KName.CLIENT_ID, userKey))
            /* delete OUser record */
            .compose(item -> oUserDao.deleteByIdAsync(Ux.toJson(item).getString(KName.KEY)))
            /* delete related role records */
            .compose(oUserFlag -> rUserRoleDao.deleteByAsync(new JsonObject().put(KName.USER_ID, userKey)))
            /* delete related group records */
            .compose(rUserRoleFlag -> rUserGroupDao.deleteByAsync(new JsonObject().put(KName.USER_ID, userKey)))
            /* delete SUser record */
            .compose(rUserGroupFlag -> sUserDao.deleteByIdAsync(userKey));
    }
}
