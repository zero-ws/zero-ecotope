package io.zerows.extension.module.rbac.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.component.acl.relation.LinkManager;
import io.zerows.extension.module.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.module.rbac.servicespec.UserStub;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;

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
            .compose(LinkManager.refExtension()::fetchAsync)
            /* Relation for roles / groups */
            .compose(LinkManager.refPerms()::fetchAsync)
            /* Setting for user */
            .compose(LinkManager.refSetting()::fetchAsync);
    }

    // ================== Basic Part of S_User ================

    /*
     * 只附加更新关联对象，该API不更新和账号本身相关的内容如
     * -- Role
     * -- Group
     * -- OUser
     */
    @Override
    public Future<JsonObject> updateInformation(final String userId, final JsonObject params) {
        final SUser user = Ux.fromJson(params, SUser.class);
        user.setId(userId);
        return DB.on(SUserDao.class).updateAsync(userId, user)
            .compose(userInfo -> LinkManager.refExtension().saveAsync(userInfo, params));
    }

    @Override
    public Future<JsonObject> createUser(final JsonObject params) {
        final SUser user = Ux.fromJson(params, SUser.class);
        /*
         * 创建账号时如果没有密码则设置初始密码
         * 初始密码配置位置：plugin/-rbac/configuration.json
         */
        if (Objects.isNull(user.getPassword())) {
            user.setPassword(Sc.valuePassword());
        } else {
            final String encrypt = Sc.valuePassword(user.getPassword());
            user.setPassword(encrypt);
        }
        final KRef refer = new KRef();
        return DB.on(SUserDao.class).insertAsync(user)
            .compose(refer::future)
            .compose(entity -> Ux.futureJ(refer.<SUser>get().setPassword(null)));
    }

    @Override
    public Future<Boolean> deleteUser(final String userKey) {
        return DB.on(SUserDao.class).deleteByIdAsync(userKey)
            .compose(nil -> LinkManager.role().removeAsync(userKey))
            .compose(nil -> LinkManager.group().removeAsync(userKey));
    }
}
