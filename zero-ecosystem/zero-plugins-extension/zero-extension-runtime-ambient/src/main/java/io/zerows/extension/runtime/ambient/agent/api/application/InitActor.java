package io.zerows.extension.runtime.ambient.agent.api.application;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.corpus.database.atom.Database;
import io.zerows.epoch.program.fn.Fx;
import io.zerows.epoch.program.Ut;
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
        return Fx.ifBool(database.test());
    }
}
