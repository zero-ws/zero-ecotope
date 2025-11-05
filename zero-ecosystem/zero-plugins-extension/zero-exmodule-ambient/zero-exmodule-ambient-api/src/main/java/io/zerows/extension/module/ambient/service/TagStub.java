package io.zerows.extension.module.ambient.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.domain.tables.pojos.RTagObject;

import java.util.List;

/**
 * @author lang : 2023-09-28
 */
public interface TagStub {

    Future<JsonObject> saveAsync(JsonObject body);

    Future<Boolean> deleteAsync(String key);

    Future<List<RTagObject>> fetchAsync(String modelId, String modelKey);
}
