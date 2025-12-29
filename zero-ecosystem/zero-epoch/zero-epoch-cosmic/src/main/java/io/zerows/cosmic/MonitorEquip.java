package io.zerows.cosmic;

import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

/**
 * 监控专用注册模式（纯监控配置预处理）
 *
 * @author lang : 2025-12-29
 */
public interface MonitorEquip {

    void registryOption(JsonObject configJ, VertxOptions options);
}
