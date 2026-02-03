package io.zerows.extension.module.rbac.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.UArray;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.domain.tables.daos.RGroupRoleDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SGroupDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RGroupRole;
import io.zerows.extension.module.rbac.domain.tables.pojos.SGroup;
import io.zerows.extension.module.rbac.servicespec.GroupStub;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GroupService implements GroupStub {

    @Override
    public Future<JsonArray> fetchRoleIdsAsync(final String groupKey) {
        log.info("{} REL/查找关系 ( 组 - 角色 ) / Group = {}, Mode 异步", ScConstant.K_PREFIX, groupKey);
        return Ke.umALink(ScAuthKey.F_GROUP_ID, groupKey, RGroupRoleDao.class);
    }

    @Override
    public JsonArray fetchRoleIds(final String groupKey) {
        log.info("{} REL/查找关系 ( 组 - 角色 ) / Group = {}, Mode 同步", ScConstant.K_PREFIX, groupKey);
        final List<RGroupRole> relations = DB.on(RGroupRoleDao.class)
            .fetch(ScAuthKey.F_GROUP_ID, groupKey);
        return UArray.create(Ux.toJson(relations))
            .remove(ScAuthKey.F_GROUP_ID).to();
    }

    @Override
    public SGroup fetchParent(final String groupKey) {
        final ADB dao = DB.on(SGroupDao.class);
        final SGroup current = dao.fetchById(groupKey);
        return null == current ? null :
            dao.fetchById(current.getParentId());
    }

    @Override
    public List<SGroup> fetchChildren(final String groupKey) {
        final ADB dao = DB.on(SGroupDao.class);
        return dao.fetch(ScAuthKey.F_PARENT_ID, groupKey);
    }

    @Override
    public Future<JsonArray> fetchGroupsBySigma(final String sigma) {
        return DB.on(SGroupDao.class)
            /* Fetch by sigma */
            .<SGroup>fetchAsync(KName.SIGMA, sigma)
            /* Get Result */
            .compose(Ux::futureA);
    }
}
