package io.zerows.extension.runtime.ambient.agent.api.application;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.fn.RFn;
import io.zerows.core.util.Ut;
import io.zerows.core.database.atom.Database;
import io.zerows.extension.runtime.ambient.agent.service.application.InitStub;
import io.zerows.extension.runtime.ambient.eon.Addr;
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

    @Address(Addr.Init.CONNECT)
    public Future<JsonObject> connect(final JsonObject data) {
        final Database database = Ut.deserialize(data, Database.class);
        return RFn.ifBool(database.test());
    }
}
