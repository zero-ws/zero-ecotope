package io.zerows.extension.runtime.report.uca.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-04
 */
public interface ROutComponent {

    Future<ConcurrentMap<String, Object>> outAsync(JsonArray dataSource,
                                                   JsonObject parameters);
}
