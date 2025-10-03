package io.zerows.extension.commerce.rbac.uca.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.program.Ux;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.support.Ut;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.RUserRole;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.extension.runtime.skeleton.secure.Tie;

import java.util.List;
import java.util.stream.Collectors;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class TieRole implements Tie<String, JsonArray> {
    @Override
    public Future<JsonArray> identAsync(final JsonObject userJ) {
        final String userKey = Ut.valueString(userJ, KName.KEY);
        return Ke.umALink(AuthKey.F_USER_ID, userKey, RUserRoleDao.class, RUserRole::getPriority)
            .compose(result -> {
                final JsonArray roles = new JsonArray();
                result.stream().map(RUserRole::getRoleId).forEach(roles::add);
                return Ux.future(roles);
            });
    }

    @Override
    public Future<JsonArray> identAsync(final String userKey) {
        // Fetch related role
        LOG.Auth.info(this.getClass(), AuthMsg.RELATION_USER_ROLE, userKey);
        return Ke.umALink(AuthKey.F_USER_ID, userKey, RUserRoleDao.class);
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
            .put(AuthKey.F_USER_ID, userKey);
        // Remove & Insert
        final UxJooq jq = Ux.Jooq.on(RUserRoleDao.class);
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
