package io.zerows.extension.module.rbac.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.RRolePermDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RRolePerm;
import io.zerows.extension.module.rbac.servicespec.RightsStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class RightsService implements RightsStub {
    @Override
    public Future<JsonArray> saveRoles(final String roleId, final JsonArray data) {
        // 1. make up role-perm entity
        final List<RRolePerm> rolePerms = Ut.itJString(data)
            .filter(Ut::isNotNil)
            .map(perm -> new JsonObject().put(KName.Rbac.PERM_ID, perm).put(KName.Rbac.ROLE_ID, roleId))
            .map(rolePerm -> Ux.fromJson(rolePerm, RRolePerm.class))
            .collect(Collectors.toList());
        // 2. delete old ones and insert new ones
        return this.removeRoles(roleId)
            .compose(result -> DB.on(RRolePermDao.class)
                .insertAsync(rolePerms)
                .compose(Ux::futureA)
            );
    }

    @Override
    public Future<Boolean> removeRoles(final String roleId) {
        return DB.on(RRolePermDao.class)
            .deleteByAsync(new JsonObject().put(KName.Rbac.ROLE_ID, roleId));
    }

    @Override
    public Future<JsonArray> fetchAsync(final String sigma) {
        /*
         * Build condition of `sigma`
         */
        final JsonObject condition = new JsonObject();
        condition.put(KName.SIGMA, sigma);
        /*
         * Permission Groups processing
         *「Upgrade」
         * Old method because `GROUP` is in S_PERMISSION
         * return Ux.Jooq.join(SPermissionDao.class).countByAsync(condition, "group", "identifier");
         * New version: S_PERM_SET processing
         */
        return Ux.futureA(); // DB.on(SPermSetDao.class).fetchJAsync(condition);
    }

    // ======================= Basic Three Method =============================
}
