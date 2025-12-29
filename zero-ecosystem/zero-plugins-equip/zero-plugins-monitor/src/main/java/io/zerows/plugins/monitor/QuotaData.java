package io.zerows.plugins.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 对应配置片段
 * <pre>
 *     monitor:
 *       clients:
 *       - name: ???
 *         component: ???
 *         enabled: true/false
 *       roles:
 *       - id: ???
 *         component: ???
 *         configYaml: {@link JsonObject}
 * </pre>
 *
 * @author lang : 2025-12-29
 */
public interface QuotaData {

    Future<Boolean> register(JsonObject config, MeterRegistry registry);
}
