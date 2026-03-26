package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.module.rbac.servicespec.AuthRegisterStub;
import jakarta.inject.Inject;

@Queue
public class RegisterActor {

    @Inject
    private transient AuthRegisterStub stub;

    @Address(Addr.Auth.REGISTER)
    public Future<JsonObject> register(final JsonObject params) {
        return this.stub.register(params);
    }
}
