package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface CallbackStub {

    Future<JsonObject> backAsync(JsonObject request);
}
