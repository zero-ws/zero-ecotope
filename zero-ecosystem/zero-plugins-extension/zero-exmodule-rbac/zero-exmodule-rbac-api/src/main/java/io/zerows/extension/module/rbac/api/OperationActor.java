package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.SActionDao;
import io.zerows.extension.module.rbac.servicespec.ActionStub;
import io.zerows.program.Ux;
import jakarta.inject.Inject;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class OperationActor {

    @Inject
    private transient ActionStub actionStub;

    @Address(Addr.Action.ACTION_SEEK)
    public Future<JsonArray> searchAuthorized(final String sigma, final JsonObject params) {
        final String keyword = params.getString("keyword");
        return this.actionStub.searchAuthorized(keyword, sigma).compose(Ux::futureA);
    }

    @Address(Addr.Action.ACTION_READY)
    public Future<JsonArray> searchAll(final String sigma, final JsonObject params) {
        final String keyword = params.getString("keyword");
        return this.actionStub.searchAll(keyword, sigma).compose(Ux::futureA);
    }

    @Address(Addr.Action.BY_PERM)
    public Future<JsonArray> searchByPerm(final String pid) {
        return DB.on(SActionDao.class).fetchAsync("permissionId", pid)
            .compose(Ux::futureA);
    }
}
