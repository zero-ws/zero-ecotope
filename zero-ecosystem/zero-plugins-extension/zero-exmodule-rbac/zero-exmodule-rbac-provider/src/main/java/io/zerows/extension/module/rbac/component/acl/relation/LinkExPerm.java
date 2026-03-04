package io.zerows.extension.module.rbac.component.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.hashing.HashingStrategy;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.boot.Sc;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserGroupDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SUserDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.skeleton.spi.ScLink;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Collection;
import java.util.Objects;

public class LinkExPerm implements ScLink.Extension<String> {
    /*
     * userJ -> User + Extension JsonObject
     * This method will extract `roles` & `groups` from system
     */
    @Override
    public Future<JsonObject> fetchAsync(final JsonObject userJ) {
        final String key = Ut.vId(userJ);
        return LinkManager.role().fetchAsync(key).compose(roles -> {
            userJ.put(KName.ROLE, Ut.encryptBase64(roles.encodePrettily()));
            return Ux.future();
        }).compose(nil -> LinkManager.group().fetchAsync(key)).compose(groups -> {
            userJ.put(KName.GROUP, Ut.encryptBase64(groups.encodePrettily()));
            return Ux.future(userJ);
        });
    }

    @Override
    public Future<JsonObject> fetchAsync(final String key) {
        return DB.on(SUserDao.class).fetchJByIdAsync(key)
            .compose(userJ -> {
                /* delete attribute: password from user information */
                userJ.remove(KName.PASSWORD);
                return Ux.future(userJ);
            })
            .compose(userJ -> LinkManager.role().fetchAsync(userJ)
                // roles -> JsonArray
                .compose(roles -> Ux.future(userJ.put(KName.ROLES, roles))))
            .compose(userJ -> LinkManager.group().fetchAsync(userJ)
                // groups -> JsonArray
                .compose(groups -> Ux.future(userJ.put(KName.GROUPS, groups)))
            );
    }

    @Override
    public Future<JsonObject> saveAsync(final String key, final JsonObject updatedData) {
        return this.updateAsync(key, updatedData)
            .compose(userJ -> LinkManager.role().saveAsync(key, userJ)
                // roles -> JsonArray
                .compose(roles -> Ux.future(userJ.put(KName.ROLES, roles))))
            .compose(userJ -> LinkManager.group().saveAsync(key, userJ)
                // groups -> JsonArray
                .compose(groups -> Ux.future(userJ.put(KName.GROUPS, groups)))
            );
    }

    // ----------------------- Extract Data -----------------------
    /*
     * 此处读取只读取 groups，不读取 roles，最终数据格式如：
     * {
     *     "userId": "xxx",
     *     "groupId": "xxx"
     * }
     */
    @Override
    public Future<JsonArray> fetchAsync(final Collection<String> keys) {
        return DB.on(RUserGroupDao.class).fetchJInAsync(ScAuthKey.F_USER_ID, keys);
    }

    private Future<JsonObject> updateAsync(final String userKey, final JsonObject params) {
        /* Merge original here */
        final ADB jq = DB.on(SUserDao.class);
        return jq.<SUser>fetchByIdAsync(userKey).compose(queried -> {
            if (Objects.isNull(queried)) {
                return Ux.futureJ();
            }
            if (params.containsKey(KName.PASSWORD)) {
                final String password = params.getString(KName.PASSWORD);
                if (Objects.nonNull(password) && !password.isEmpty()) {
                    final String valuePassword = Sc.valuePassword(password);
                    params.put(KName.PASSWORD, valuePassword);
                }
            }
            final SUser updated = Ux.updateT(queried, params);

            /* User Saving here */
            return jq.updateJAsync(userKey, updated).compose(userJ -> {
                // Be sure the response contains `roles` and `groups`
                userJ.put(KName.ROLES, params.getValue(KName.ROLES));
                userJ.put(KName.GROUPS, params.getValue(KName.GROUPS));
                return Ux.future(userJ);
            });
        });
    }
}
