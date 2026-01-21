package io.zerows.extension.module.rbac.component.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.ADB;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.extension.module.rbac.domain.tables.daos.RUserGroupDao;
import io.zerows.extension.module.rbac.domain.tables.pojos.RUserGroup;
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
public class TieGroup implements ScTie<String, JsonArray> {
    @Override
    public Future<JsonArray> identAsync(final JsonObject userJ) {
        final String userKey = Ut.valueString(userJ, KName.KEY);
        return Ke.umALink(ScAuthKey.F_USER_ID, userKey, RUserGroupDao.class, RUserGroup::getPriority)
            .compose(result -> {
                final JsonArray groups = new JsonArray();
                result.stream().map(RUserGroup::getGroupId).forEach(groups::add);
                return Ux.future(groups);
            });
    }

    @Override
    public Future<JsonArray> identAsync(final String userKey) {
        log.info("{} REL/查找关系 ( 用户 - 组 ）/ 用户 id = {}", ScConstant.K_PREFIX, userKey);
        return Ke.umALink(ScAuthKey.F_USER_ID, userKey, RUserGroupDao.class);
    }

    @Override
    @SuppressWarnings("all")
    public Future<JsonArray> identAsync(final String userKey, final JsonObject userJ) {
        // Update Related Groups
        final JsonArray groups = Ut.valueJArray(userJ, KName.GROUPS);
        if (Ut.isNil(groups)) {
            return Ux.futureA();
        }

        final JsonObject conditionJ = new JsonObject()
            .put(ScAuthKey.F_USER_ID, userKey);
        /* Remove & Insert */
        final ADB jq = DB.on(RUserGroupDao.class);
        /* Delete Related Groups */
        return jq.deleteByAsync(conditionJ).compose(nil -> {
            /* Insert Related Groups */
            final List<String> groupIds = groups.getList();
            final List<RUserGroup> inserted = groupIds.stream()
                .map(groupId -> new RUserGroup()
                    .setUserId(userKey)
                    .setGroupId(groupId)
                    .setPriority(groupIds.indexOf(groupId)))
                .collect(Collectors.toList());
            return jq.insertAsync(inserted);
        }).compose(nil -> Ux.future(groups));
    }
}
