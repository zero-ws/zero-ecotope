package io.zerows.extension.commerce.rbac.uca.acl.relation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.database.jooq.operation.DBJooq;
import io.zerows.extension.commerce.rbac.domain.tables.daos.RUserGroupDao;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.RUserGroup;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.eon.AuthMsg;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.extension.runtime.skeleton.secure.Tie;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.List;
import java.util.stream.Collectors;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class TieGroup implements Tie<String, JsonArray> {
    @Override
    public Future<JsonArray> identAsync(final JsonObject userJ) {
        final String userKey = Ut.valueString(userJ, KName.KEY);
        return Ke.umALink(AuthKey.F_USER_ID, userKey, RUserGroupDao.class, RUserGroup::getPriority)
            .compose(result -> {
                final JsonArray groups = new JsonArray();
                result.stream().map(RUserGroup::getGroupId).forEach(groups::add);
                return Ux.future(groups);
            });
    }

    @Override
    public Future<JsonArray> identAsync(final String userKey) {
        LOG.Auth.debug(this.getClass(), AuthMsg.RELATION_GROUP, userKey);
        return Ke.umALink(AuthKey.F_USER_ID, userKey, RUserGroupDao.class);
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
            .put(AuthKey.F_USER_ID, userKey);
        /* Remove & Insert */
        final DBJooq jq = DB.on(RUserGroupDao.class);
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
