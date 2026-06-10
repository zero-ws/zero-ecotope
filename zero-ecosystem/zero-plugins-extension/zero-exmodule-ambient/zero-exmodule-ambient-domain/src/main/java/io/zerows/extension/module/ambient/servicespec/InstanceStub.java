package io.zerows.extension.module.ambient.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface InstanceStub {

    Future<JsonArray> search(JsonObject criteria);

    Future<JsonObject> getById(String key);

    Future<JsonObject> upsert(JsonObject instanceData);

    Future<JsonObject> updateStatus(String key, String status);

    Future<JsonObject> delete(String key);
}
