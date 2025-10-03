package io.zerows.extension.commerce.rbac.agent.service.business;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.component.log.Annal;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.UxJooq;
import io.zerows.epoch.metadata.UArray;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RGroupRoleDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SGroupDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.RGroupRole;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SGroup;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.program.Ux;

import java.util.ArrayList;
import java.util.List;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

public class GroupService implements GroupStub {

    private static final Annal LOGGER = Annal.get(GroupService.class);

    @Override
    public Future<JsonArray> fetchRoleIdsAsync(final String groupKey) {
        LOG.Auth.info(LOGGER, AuthMsg.RELATION_GROUP_ROLE, groupKey, "Async");
        return Ke.umALink(AuthKey.F_GROUP_ID, groupKey, RGroupRoleDao.class);
    }

    @Override
    public JsonArray fetchRoleIds(final String groupKey) {
        LOG.Auth.info(LOGGER, AuthMsg.RELATION_GROUP_ROLE, groupKey, "Sync");
        final List<RGroupRole> relations = Ux.Jooq.on(RGroupRoleDao.class)
            .fetch(AuthKey.F_GROUP_ID, groupKey);
        return UArray.create(Ux.toJson(relations))
            .remove(AuthKey.F_GROUP_ID).to();
    }

    @Override
    public SGroup fetchParent(final String groupKey) {
        final UxJooq dao = Ux.Jooq.on(SGroupDao.class);
        if (null == dao) {
            return null;
        }
        final SGroup current = dao.fetchById(groupKey);
        return null == current ? null :
            dao.fetchById(current.getParentId());
    }

    @Override
    public List<SGroup> fetchChildren(final String groupKey) {
        final UxJooq dao = Ux.Jooq.on(SGroupDao.class);
        if (null == dao) {
            return new ArrayList<>();
        }
        return dao.fetch(AuthKey.F_PARENT_ID, groupKey);
    }

    @Override
    public Future<JsonArray> fetchGroups(final String sigma) {
        return Ux.Jooq.on(SGroupDao.class)
            /* Fetch by sigma */
            .<SGroup>fetchAsync(KName.SIGMA, sigma)
            /* Get Result */
            .compose(Ux::futureA);
    }
}
