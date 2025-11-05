package io.zerows.extension.module.finance.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2024-01-11
 */
public interface InCancelStub {

    Future<Boolean> cancelAsync(JsonArray keys, JsonObject params);

    Future<Boolean> cancelAsync(JsonArray keys, String key, JsonObject params);
}
