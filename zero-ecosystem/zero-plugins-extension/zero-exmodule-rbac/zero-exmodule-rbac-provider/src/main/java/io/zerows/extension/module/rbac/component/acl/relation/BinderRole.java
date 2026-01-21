package io.zerows.extension.module.rbac.component.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SRoleDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RUserRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SUser;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class BinderRole extends AbstractBind<SRole> {

    BinderRole(final String sigma) {
        super(sigma);
    }

    @Override
    public Future<JsonArray> bindAsync(final List<SUser> users, final JsonArray inputData) {
        if (users.isEmpty()) {
            return Ux.futureA();
        }
        // Only Exist Roles Delete The RelationShip
        final List<SUser> deleteUser = new ArrayList<>();
        users.forEach(user -> {
            Ut.itJArray(inputData).forEach(input -> {
                final String roles = input.getString(KName.ROLES);
                final String userName = input.getString(KName.USERNAME);
                if (Ut.isNotNil(roles) && userName.equals(user.getUsername())) {
                    deleteUser.add(user);
                }
            });
        });
        return this.purgeAsync(deleteUser, RUserRoleDao.class, ScAuthKey.F_USER_ID)
            .compose(nil -> this.mapAsync(inputData, SRoleDao.class, KName.ROLES))
            .compose(roleMap -> {
                /*
                 * Build for each user
                 */
                final List<RUserRole> relationList = new ArrayList<>();
                users.forEach(user -> {
                    final List<SRole> roles = roleMap.getOrDefault(user.getUsername(), new ArrayList<>());
                    Ut.itList(roles, (role, index) -> {
                        final RUserRole relation = new RUserRole();
                        relation.setRoleId(role.getKey());
                        relation.setUserId(user.getKey());
                        relation.setPriority(index);
                        relationList.add(relation);
                    });
                    /*
                     * Building relation ship
                     */
                    log.info("{} REL | 用户名 = {} / 角色数量 = {}", ScConstant.K_PREFIX, user.getUsername(), roles.size());
                });
                return Ux.future(relationList);
            })
            .compose(DB.on(RUserRoleDao.class)::insertAsync)
            .compose(nil -> Ux.futureA(users));
    }

    @Override
    protected Function<SRole, String> keyFn() {
        return SRole::getKey;
    }

    @Override
    protected Function<SRole, String> valueFn() {
        return SRole::getName;
    }
}
