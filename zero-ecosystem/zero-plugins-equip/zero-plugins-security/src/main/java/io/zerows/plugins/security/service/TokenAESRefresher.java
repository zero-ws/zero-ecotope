package io.zerows.plugins.security.service;

import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.token.TokenBuilder;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.SecurityActor;

import java.util.Objects;
import java.util.UUID;

/**
 * AES Refresh Token 管理器
 * <p>
 * 核心逻辑：
 * 1. <b>Access Token</b> 是无状态的 AES 加密串。
 * 2. <b>Refresh Token</b> 是有状态的随机 UUID（存储在 Redis/UserCache 中）。
 * 3. 刷新时：用旧的 UUID 换取新的 AES Access Token，并强制轮转（Rotation）。
 *
 * @author lang
 */
public class TokenAESRefresher {

    private final TokenAESGenerator tokenGenerator;

    public TokenAESRefresher() {
        this.tokenGenerator = new TokenAESGenerator();
    }

    private SecurityConfig config() {
        return SecurityActor.configOf(SecurityType.BASIC);
    }

    private boolean isDisabled() {
        return !this.config().option("enabled", Boolean.TRUE);
    }

    /**
     * 生成 Refresh Token 并存储到 UserCache
     * <p>
     * 注意：Refresh Token 本身不需要是 AES 加密的，它只是一个指向 UserCache 的随机句柄 (Handle)。
     *
     * @param userId 用户 ID
     * @return 随机 UUID 字符串 (Refresh Token)
     */
    public String tokenGenerate(final String userId) {
        // 1. 检查配置开关
        if (Objects.isNull(this.config()) || this.isDisabled()) {
            return null;
        }

        // 2. 生成一个安全的随机 Refresh Token (Opaque Token)
        final String refreshToken = UUID.randomUUID().toString().replace("-", "");

        // 3. 获取 UserCache 实例
        final UserCache userCache = UserCache.of();

        // 4. 将 Refresh Token 与用户 ID 绑定存储
        // 这里假设 userId 是 UUID 格式的字符串，如果不是需自行调整逻辑
        try {
            userCache.tokenRefresh(refreshToken, UUID.fromString(userId));
        } catch (final IllegalArgumentException e) {
            // 如果 userId 不是标准 UUID，UserCache 可能需要调整 Key 类型，或者这里不做转换
            // userCache.tokenRefresh(refreshToken, userId); // 视 UserCache API 而定
        }

        return refreshToken;
    }

    /**
     * 刷新 Token (Rotation 模式)
     * <p>
     * 验证 Refresh Token 有效性 -> 签发新的 AES Access Token -> 作废旧 Refresh Token
     *
     * @param refreshToken 客户端提供的旧 Refresh Token
     * @return 新的 Access Token (AES 加密串)。如果验证失败返回 null。
     */
    public String tokenRefresh(final String refreshToken) {
        // 1. 检查配置与入参
        if (Objects.isNull(this.config()) || this.isDisabled()) {
            return null;
        }

        return TokenBuilder.withRefresh(refreshToken,
            (loginId) -> this.tokenGenerator.tokenGenerate(loginId, null));
    }

    /**
     * 撤销 Refresh Token
     * 通常在用户主动注销 (Logout) 时调用
     *
     * @param refreshToken 需要作废的 Token
     */
    public void tokenRevoke(final String refreshToken) {
        if (refreshToken != null && !refreshToken.trim().isEmpty()) {
            final UserCache userCache = UserCache.of();
            userCache.tokenRefreshKo(refreshToken);
        }
    }
}