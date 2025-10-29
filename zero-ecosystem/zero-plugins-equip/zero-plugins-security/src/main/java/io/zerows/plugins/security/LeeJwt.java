package io.zerows.plugins.security;

import io.vertx.core.json.JsonObject;
import io.zerows.sdk.security.Lee;

/**
 * 默认编解码处理器，主要用于 JWT 的编解码，此处的配置来源于启动过程中的配置注入：
 * <pre>
 *     vertx:
 *       security:
 *         jwt:
 *           options: {@link JsonObject} - JWT 配置项
 * </pre>
 *
 * @author lang : 2025-10-29
 */
public class LeeJwt implements Lee {
    @Override
    public String encode(final JsonObject payload) {
        return "";
    }

    @Override
    public JsonObject decode(final String token) {
        return null;
    }
}
