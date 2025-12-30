package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
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

    @Address(Addr.Authority.ACTION_SEEK)
    public Future<JsonArray> searchAuthorized(final String sigma, final JsonObject params) {
        final String keyword = params.getString("keyword");
        return this.actionStub.searchAuthorized(keyword, sigma).compose(Ux::futureA);
    }

    @Address(Addr.Authority.ACTION_READY)
    public Future<JsonArray> searchAll(final String sigma, final JsonObject params) {
        final String keyword = params.getString("keyword");
        return this.actionStub.searchAll(keyword, sigma).compose(Ux::futureA);
    }
}
