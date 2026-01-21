package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.XHeader;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.epoch.web.Account;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.SPermSet;
import io.zerows.extension.module.rbac.servicespec.PermStub;
import io.zerows.extension.module.rbac.servicespec.RightsStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
@Slf4j
public class PermActor {

    @Inject
    private transient PermStub stub;

    @Inject
    private transient RightsStub setStub;

    /*
     * Calculate Permission Groups
     */
    @Address(Addr.Authority.PERMISSION_GROUP)
    public Future<JsonArray> calculate(final XHeader header) {
        return this.setStub.fetchAsync(header.getSigma());
    }

    /*
     * Two steps, the input json is:
     * Action Part:
     * {
     *     "removed": [
     *         "action1",
     *         "action2"
     *     ],
     *     "relation":{
     *         "action3": "permission1",
     *         "action4": "permission2"
     *     }
     * }
     * Permission Part:
     * {
     *     "group": "xxxx",
     *     "data": [
     *
     *     ]
     * }
     *
     * The steps is as following:
     *
     * 1. Permission Sync ( Removed, Add New, Update )
     * 2. Action Sync ( Add New, Relation Processing )
     *
     * The old workflow is 3 tables:
     * S_PERMISSION / S_ACTION / S_RESOURCE
     *
     * The new workflow is 4 tables calculation:
     * S_PERMISSION / S_ACTION / S_RESOURCE / S_PERM_SET
     *
     * The detail workflow should be as following:
     *** 1）Check whether current S_PERM_SET here, the unique condition is ( name + code, here code is permissions code )
     *** -- If existing, update current S_PERM_SET
     *** -- If missing, create new S_PERM_SET
     ***
     *** 2) Remove all permissions from current S_PERM_SET only
     ***
     *** Build new based SPermSet entity that include
     *** a）name
     *** b) createdAt / createdBy
     *** c) language / sigma
     */
    @Address(Addr.Authority.PERMISSION_DEFINITION_SAVE)
    public Future<JsonObject> saveDefinition(final JsonObject processed,
                                             final XHeader header, final User user) {
        final String sigma = header.getSigma();
        log.info("{} 权限更新：{} / sigma = {}", ScConstant.K_PREFIX, processed.encode(), sigma);

        // Permission Data
        final JsonArray permissions = Ut.valueJArray(processed.getJsonArray(KName.DATA));
        final String group = processed.getString("group");
        final String type = processed.getString("type");

        // Action Data
        final JsonArray removed = Ut.valueJArray(processed.getJsonArray("removed"));
        final JsonObject relation = Ut.valueJObject(processed.getJsonObject("relation"));

        final String userKey = Account.userId(user);

        // SPermSet
        final SPermSet permSet = new SPermSet();
        permSet.setName(group);
        permSet.setType(type);
        permSet.setActive(Boolean.TRUE);
        permSet.setSigma(sigma);
        permSet.setLanguage(header.getLanguage());
        permSet.setUpdatedAt(LocalDateTime.now());
        permSet.setUpdatedBy(userKey);

        return this.setStub.saveDefinition(permissions, permSet)                       // Permission Process
            .compose(nil -> this.stub.syncAsync(removed, relation, userKey))       // Action Process
            .compose(nil -> Ux.future(relation));
    }

    @Address(Addr.Authority.PERMISSION_BY_ROLE)
    public Future<JsonArray> fetchAsync(final String roleId) {
        return DB.on(RRolePermDao.class)
            .fetchAsync(KName.Rbac.ROLE_ID, roleId)
            .compose(Ux::futureA);
    }

    @Address(Addr.Authority.PERMISSION_SAVE)
    public Future<JsonArray> savePerm(final String roleId, final JsonArray permissions) {
        /*
         * Sync operation join permissions
         */
        return this.stub.syncAsync(permissions, roleId);
    }

    // ======================= CRUD Replace =============================
    @Address(Addr.Perm.PERMISSION_UN_READY)
    public Future<JsonObject> searchUnReady(final JsonObject query, final XHeader header) {
        return this.stub.searchAsync(query, header.getSigma());
    }

    @Address(Addr.Perm.BY_ID)
    public Future<JsonObject> fetch(final String key) {
        return this.stub.fetchAsync(key);
    }

    @Address(Addr.Perm.ADD)
    public Future<JsonObject> add(final JsonObject body) {
        return this.stub.createAsync(body);
    }

    @Address(Addr.Perm.EDIT)
    public Future<JsonObject> edit(final String key, final JsonObject body) {
        return this.stub.updateAsync(key, body);
    }

    @Address(Addr.Perm.DELETE)
    public Future<Boolean> delete(final String key, final User user) {
        final String userKey = Account.userId(user);
        return this.stub.deleteAsync(key, userKey);
    }
}
