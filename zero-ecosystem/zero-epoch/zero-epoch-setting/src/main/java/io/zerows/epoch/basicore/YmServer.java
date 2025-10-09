package io.zerows.epoch.basicore;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.VertxYml;
import io.zerows.epoch.configuration.EquipItem;
import lombok.Data;

import java.io.Serializable;

/**
 * 🌐 服务器配置类
 * {@link VertxYml.server}
 * <pre>
 *     📋 服务器属性默认值表：
 *     ┌─────────────────────┬─────────────────────┬─────────────────────┐
 *          🏷️ 配置项              📝 默认值              🎯 说明
 *     ├─────────────────────┼─────────────────────┼─────────────────────┤
 *          port                  6083                 服务器端口号
 *          address               0.0.0.0              服务器绑定地址（广域）
 *     └─────────────────────┴─────────────────────┴─────────────────────┘
 * </pre>
 * 提供默认的服务属性信息
 * <pre>
 *     🎯 功能说明：
 *     - 配置服务器端口号和绑定地址
 *     - 管理服务器选项配置
 *     - 提供 WebSocket 配置支持
 * </pre>
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmServer implements Serializable, EquipItem {
    private int port = 6083;
    private String address = "0.0.0.0";
    private JsonObject options = new JsonObject();
    private YmSession session;
    private YmWebSocket websocket = new YmWebSocket();

    @Override
    public JsonObject combined() {
        final JsonObject config = this.options.copy();
        // 此处对接 HttpServerOptions
        config.put("port", this.port);
        config.put("host", this.address);
        return config;
    }
}
