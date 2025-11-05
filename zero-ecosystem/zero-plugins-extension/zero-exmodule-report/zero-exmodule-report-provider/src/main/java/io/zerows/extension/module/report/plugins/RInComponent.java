package io.zerows.extension.module.report.plugins;

import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2024-11-04
 */
public interface RInComponent {

    Future<Kv<String, Object>> prepare(JsonObject params, JsonObject inConfig);
}
