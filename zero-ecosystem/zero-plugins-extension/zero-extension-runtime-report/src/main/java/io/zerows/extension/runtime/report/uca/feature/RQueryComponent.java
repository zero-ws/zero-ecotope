package io.zerows.extension.runtime.report.uca.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.ConcurrentMap;

/**
 * @author Yu : 2024-11-27
 */
public interface RQueryComponent {

    Future<JsonArray> dataAsync(JsonArray dataSource, JsonObject parameters);
}
