package io.zerows.extension.runtime.report.uca.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.common.program.Kv;

/**
 * @author lang : 2024-11-04
 */
public interface RInComponent {

    Future<Kv<String, Object>> prepare(JsonObject params, JsonObject inConfig);
}
