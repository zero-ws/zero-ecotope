package io.zerows.sdk.security;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.Credentials;
import io.zerows.platform.enums.SecurityType;

import java.io.Serializable;

/**
 * 🔐 核心令牌配置数据结构的专用接口
 * <p>
 * 该接口定义了系统中通用令牌（Token）的核心属性和行为，用于封装与令牌相关的数据和操作。
 * 它旨在提供一个统一的抽象，以支持不同类型的令牌（如 JWT, Bearer Token 等）。
 * </p>
 */
public interface Token extends Serializable {

    static String encode(final JsonObject payload, final SecurityType type) {
        return TokenUtil.encode(payload, type);
    }

    static String encodeJwt(final JsonObject payload) {
        return TokenUtil.encode(payload, SecurityType.JWT);
    }

    static String encodeBasic(final JsonObject payload) {
        return TokenUtil.encode(payload, SecurityType.BASIC);
    }

    static JsonObject decode(final String token, final SecurityType type) {
        return TokenUtil.decode(token, type);
    }

    static JsonObject decodeJwt(final String token) {
        return TokenUtil.decode(token, SecurityType.JWT);
    }

    static JsonObject decodeBasic(final String token) {
        return TokenUtil.decode(token, SecurityType.BASIC);
    }

    /**
     * 🏷️ 获取令牌的原始字符串值。
     * <p>
     * 此值通常代表编码后的令牌内容（例如 JWT 的 base64 编码字符串）。
     * </p>
     *
     * @return 令牌的字符串表示形式，如果未设置则可能为 null。
     */
    default String token() {
        // 默认场景之下不返回 token 的值，此处作为占位符，特殊 Token 可返回
        return null;
    }

    /**
     * 📨 生成标准的 `Authorization` HTTP 请求头的值。
     * <p>
     * 此方法根据令牌值构建用于 HTTP 请求认证的标准 `Authorization` 头。
     * 例如，对于 Bearer Token，它通常返回 "Bearer <token_value>" 格式的字符串。
     * </p>
     *
     * @return 格式化后的 `Authorization` 请求头字符串，如果令牌无效或无法构建则可能为 null。
     */
    String authorization();

    /**
     * 👤 读取令牌中携带的用户标识信息。
     * <p>
     * 此方法尝试从令牌的数据载荷（payload）中提取用户 ID 或用户名等唯一标识符。
     * 默认实现返回 null，具体实现类应重写此方法以提供实际的用户信息提取逻辑。
     * </p>
     *
     * @return 从令牌中解析出的用户标识字符串，如果令牌不包含或无法解析用户信息，则返回 null。
     */
    default String user() {
        return null; // 默认实现不提供用户信息
    }

    /**
     * 📦 读取令牌中包含的完整数据内容。
     * <p>
     * 对于结构化令牌（如 JWT），这通常代表令牌的 payload 部分，包含了所有声明（claims）。
     * </p>
     *
     * @return 一个 {@link JsonObject}，包含令牌的完整数据内容。如果令牌不包含有效数据或无法解析，则可能返回 null。
     */
    JsonObject data();

    /**
     * 🔐 读取与该令牌关联的 Vert.x 认证凭据对象。
     * <p>
     * 此方法提供对 Vert.x 认证框架所需 {@link Credentials} 对象的访问，便于与 Vert.x 的认证机制集成。
     * </p>
     *
     * @return 一个 {@link Credentials} 对象，封装了令牌的认证信息。如果无法生成或不适用，则可能返回 null。
     */
    Credentials credentials();
}