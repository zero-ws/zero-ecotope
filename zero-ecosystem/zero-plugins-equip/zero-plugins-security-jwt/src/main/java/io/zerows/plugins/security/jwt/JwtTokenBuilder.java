package io.zerows.plugins.security.jwt;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.token.TokenBuilderBase;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.SecurityType;

import java.util.Objects;
import java.util.UUID;

/**
 * Vert.x (ZeroWS) 版 JWT Token 构建器
 * 负责 Access Token 的生成/解析 (基于 LeeJwt)
 * 负责 Refresh Token 的生命周期管理 (基于 UserCache)
 *
 * @author lang
 */
public class JwtTokenBuilder extends TokenBuilderBase {

    private static final Cc<String, JwtToken> CC_TOKEN = Cc.openThread();
    // 核心编解码器引用
    private final JwtToken codec;

    public JwtTokenBuilder() {
        // 初始化编解码器，LeeJwt 内部会自动读取 vertx.yaml 中的 security 配置
        this.codec = CC_TOKEN.pick(JwtToken::new);
    }

    /**
     * 解析 Access Token 获取用户 ID (Subject)
     *
     * @param token JWT 字符串
     * @return 用户 ID (sub)，如果无效或解析失败返回 null
     */
    @Override
    public String accessOf(final String token) {
        if (Objects.isNull(token) || token.trim().isEmpty()) {
            return null;
        }
        try {
            // 使用 LeeJwt 解码，如果验证失败内部通常会返回空 JsonObject 或抛出异常
            final JsonObject payload = this.codec.decode(token, SecurityType.JWT);

            if (payload == null || payload.isEmpty()) {
                return null;
            }

            // 检查过期时间 (Vert.x decode 方法通常已验证 exp，此处为双重保险或读取业务字段)
            // 提取 standard claim: sub
            return payload.getString("sub");
        } catch (final Exception e) {
            // 解码失败（签名错误、过期等）
            return null;
        }
    }

    /**
     * 为用户生成 Access Token
     *
     * @param userAt 用户会话对象
     * @return JWT 字符串
     */
    @Override
    public String accessOf(final UserAt userAt) {
        // 1. 确保用户已授权/数据完整 (调用父类逻辑)
        final MSUser logged = this.ensureAuthorized(userAt);

        // 2. 构造 Payload
        // Vert.x JWTAuth 会自动处理 "iat" (Issued At).
        // "exp" (Expiration) 通常由配置决定，也可以在此处覆盖 options.setExpiresInSeconds
        final JsonObject payload = new JsonObject();

        // 标准字段
        payload.put("sub", userAt.id().toString());

        // 自定义字段：保持与 Spring 版一致的登录类型标识
        payload.put("loginType", "R2MO-ZERO");

        // 如果有扩展数据，从 userAt 或 MSUser 中提取放入 ext
        // if (logged.getExtension() != null) {
        //     payload.put("ext", logged.getExtension());
        // }

        // 3. 编码生成 Token
        return this.codec.encode(payload, SecurityType.JWT);
    }

    /**
     * 生成并存储 Refresh Token
     *
     * @param userAt 用户会话对象
     * @return Refresh Token 字符串 (UUID)
     */
    @Override
    public String refreshOf(final UserAt userAt) {
        if (userAt == null || userAt.id() == null) {
            return null;
        }

        // 1. 生成随机 Token (移除横线)
        final String refreshToken = UUID.randomUUID().toString().replace("-", "");

        // 2. 存入缓存 (UserCache)
        // 注意：UserCache.of() 需要确保在 Vert.x 环境中已正确初始化
        UserCache.of().tokenRefresh(refreshToken, userAt.id());

        return refreshToken;
    }
}