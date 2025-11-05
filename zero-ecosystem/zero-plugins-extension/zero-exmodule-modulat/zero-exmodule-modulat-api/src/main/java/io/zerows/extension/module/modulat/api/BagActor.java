package io.zerows.extension.module.modulat.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.module.modulat.servicespec.BagStub;
import io.zerows.program.Ux;
import jakarta.inject.Inject;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
public class BagActor {

    @Inject
    private transient BagStub bagStub;

    @Address(Addr.Module.FETCH)
    public Future<JsonArray> bag(final String appId) {
        return this.bagStub.fetchBag(appId);
    }

    @Address(Addr.Module.BY_EXTENSION)
    public Future<JsonArray> bagByApp(final String appId) {
        return this.bagStub.fetchExtension(appId);
    }

    @Address(Addr.Module.UP_PROCESS)
    public Future<Boolean> process(final JsonObject params) {
        return Ux.futureT();
    }

    @Address(Addr.Module.UP_AUTHORIZE)
    public Future<Boolean> authorize(final JsonObject params) {
        return Ux.futureT();
    }
}
