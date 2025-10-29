package io.zerows.sdk.security;

import io.vertx.core.json.JsonObject;

/**
 * Encode/Decode Coder
 * 编码/解码的核心组件（安全专用接口）
 *
 * @author lang : 2025-10-29
 */
public interface Lee {

    String encode(JsonObject payload);

    JsonObject decode(String token);
}
