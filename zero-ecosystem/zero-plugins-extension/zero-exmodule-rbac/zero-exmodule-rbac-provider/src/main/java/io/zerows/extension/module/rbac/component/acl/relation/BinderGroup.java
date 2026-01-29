package io.zerows.extension.module.rbac.component.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserGroupDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SGroupDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RUserGroup;
import io.zerows.extension.module.rbac.domain.tables.pojos.SGroup;
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
public class BinderGroup extends AbstractBind<SGroup> {

    BinderGroup(final String sigma) {
        super(sigma);
    }

    @Override
    public Future<JsonArray> bindAsync(final List<SUser> users, final JsonArray inputData) {
        if (users.isEmpty()) {
            return Ux.futureA();
        }
        return this.purgeAsync(users, RUserGroupDao.class, ScAuthKey.F_USER_ID)
            .compose(nil -> this.mapAsync(inputData, SGroupDao.class, KName.ROLES))
            .compose(roleMap -> {
                /*
                 * Build for each user
                 */
                final List<RUserGroup> relationList = new ArrayList<>();
                users.forEach(user -> {
                    final List<SGroup> groups = roleMap.getOrDefault(user.getUsername(), new ArrayList<>());
                    Ut.itList(groups, (group, index) -> {
                        final RUserGroup relation = new RUserGroup();
                        relation.setGroupId(group.getId());
                        relation.setUserId(user.getId());
                        relation.setPriority(index);
                        relationList.add(relation);
                    });
                    /*
                     * Building relation ship
                     */
                    log.info("{} REL | 用户名 = {} / 组数量 = {}", ScConstant.K_PREFIX, user.getUsername(), groups.size());
                });
                return Ux.future(relationList);
            })
            .compose(DB.on(RUserGroupDao.class)::insertAsync)
            .compose(nil -> Ux.futureA(users));
    }

    @Override
    protected Function<SGroup, String> keyFn() {
        return SGroup::getId;
    }

    @Override
    protected Function<SGroup, String> valueFn() {
        return SGroup::getName;
    }

}
