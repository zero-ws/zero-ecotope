package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.module.ambient.service.InitStub;
import jakarta.inject.Inject;

@Queue
public class InitActor {

    @Inject
    private transient InitStub stub;

    @Address(Addr.Init.INIT)
    public Future<JsonObject> initApp(final String appId, final JsonObject data) {
        return this.stub.initCreation(appId, data);
    }

    @Address(Addr.Init.PREPARE)
    public Future<JsonObject> prepare(final String appName) {
        return this.stub.prerequisite(appName);
    }
    //
    //    @Address(Addr.Init.CONNECT)
    //    public Future<JsonObject> connect(final JsonObject data) {
    //        final JObject database = SPI.V_UTIL.deserializeJson()
    //        final Database database = Ut.deserialize(data, Database.class);
    //        return Fx.ifBool(database.test());
    //    }
}
