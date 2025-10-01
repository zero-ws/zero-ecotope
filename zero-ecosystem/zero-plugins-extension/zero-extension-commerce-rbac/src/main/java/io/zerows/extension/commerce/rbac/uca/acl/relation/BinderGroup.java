package io.zerows.extension.commerce.rbac.uca.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RUserGroupDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SGroupDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.RUserGroup;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SGroup;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SUser;
import io.zerows.extension.commerce.rbac.eon.AuthKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BinderGroup extends AbstractBind<SGroup> {

    BinderGroup(final String sigma) {
        super(sigma);
    }

    @Override
    public Future<JsonArray> bindAsync(final List<SUser> users, final JsonArray inputData) {
        if (users.isEmpty()) {
            return Ux.futureA();
        }
        return this.purgeAsync(users, RUserGroupDao.class, AuthKey.F_USER_ID)
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
                        relation.setGroupId(group.getKey());
                        relation.setUserId(user.getKey());
                        relation.setPriority(index);
                        relationList.add(relation);
                    });
                    /*
                     * Building relation ship
                     */
                    LOG.Web.info(this.getClass(), "Will web username = {1}, group size = {0}",
                        String.valueOf(groups.size()), user.getUsername());
                });
                return Ux.future(relationList);
            })
            .compose(Ux.Jooq.on(RUserGroupDao.class)::insertAsync)
            .compose(nil -> Ux.futureA(users));
    }

    @Override
    protected Function<SGroup, String> keyFn() {
        return SGroup::getKey;
    }

    @Override
    protected Function<SGroup, String> valueFn() {
        return SGroup::getName;
    }

}
