package io.zerows.extension.runtime.report.uca.feature;

import io.zerows.common.program.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2024-11-04
 */
public interface RInComponent {

    Future<Kv<String, Object>> prepare(JsonObject params, JsonObject inConfig);
}
