package io.zerows.extension.module.rbac.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface AuthRegisterStub {
    Future<JsonObject> register(JsonObject params);
}
