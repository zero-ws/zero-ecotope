package io.zerows.extension.module.rbac.component.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RUserRole;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.extension.skeleton.spi.ScTie;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class TieRole implements ScTie<String, JsonArray> {
    @Override
    public Future<JsonArray> identAsync(final JsonObject userJ) {
        final String userKey = Ut.valueString(userJ, KName.KEY);
        return Ke.umALink(ScAuthKey.F_USER_ID, userKey, RUserRoleDao.class, RUserRole::getPriority)
            .compose(result -> {
                final JsonArray roles = new JsonArray();
                result.stream().map(RUserRole::getRoleId).forEach(roles::add);
                return Ux.future(roles);
            });
    }

    @Override
    public Future<JsonArray> identAsync(final String userKey) {
        // Fetch related role
        log.info("{} REL/查找关系 ( 用户 - 角色 ）/ 用户 id = {}", ScConstant.K_PREFIX, userKey);
        return Ke.umALink(ScAuthKey.F_USER_ID, userKey, RUserRoleDao.class);
    }

    /*
     * updatedJ
     * {
     *     "...",
     *     "roles": []
     * }
     */
    @Override
    @SuppressWarnings("all")
    public Future<JsonArray> identAsync(final String userKey, final JsonObject userJ) {
        // Update Related Roles
        final JsonArray roles = Ut.valueJArray(userJ, KName.ROLES);
        if (Ut.isNil(roles)) {
            // Execute this branch when only update user information
            return Ux.futureA();
        }
        final JsonObject conditionJ = new JsonObject()
            .put(ScAuthKey.F_USER_ID, userKey);
        // Remove & Insert
        final ADB jq = DB.on(RUserRoleDao.class);
        /* Delete Related Roles */
        return jq.deleteByAsync(conditionJ).compose(nil -> {
            /* Insert Related Roles */
            final List<String> roleIds = roles.getList();
            final List<RUserRole> inserted = roleIds.stream()
                .map(roleId -> new RUserRole()
                    .setUserId(userKey)
                    .setRoleId(roleId)
                    .setPriority(roleIds.indexOf(roleId)))
                .collect(Collectors.toList());
            return jq.insertAsync(inserted);
        }).compose(nil -> Ux.future(roles));
    }
}
