package io.zerows.plugins.security.service;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.constant.LicSym;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.SecurityActor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * AES 对称加密令牌生成器（HED 静态实现）
 *
 * @author lang
 */
@Slf4j
public class TokenAESGenerator {

    // ================== 常量 ==================
    public static final String TOKEN_PREFIX = "r2a_";
    private static final String NAME_SUBJECT = "sub";
    private static final String NAME_EXPIRE = "exp"; // 时间戳（毫秒）
    private static final String NAME_ADDON_DATA = "ext";
    private static final LicSym.AlgLicenseAes AES_SPEC = LicSym.AlgLicenseAes.AES_256;

    // ================== 依赖 ==================
    private SecurityConfig config() {
        return SecurityActor.configOf(SecurityType.BASIC);
    }

    private boolean isDisabled() {
        return !this.config().option("enabled", Boolean.TRUE);
    }

    private String getAesSecret() {
        return this.config().option("aes-secret", "Z2DRXBZZEM6RGZ3GVRN8FNZGHH58E2UQ");
    }

    private long getAesExpiredMs() {
        final String expiredStr = this.config().option("aes-expired", "2h");
        final Duration expiredAt = R2MO.toDuration(expiredStr);
        return System.currentTimeMillis() + expiredAt.toMillis();
    }

    /**
     * 生成 AES 令牌
     */
    public String tokenGenerate(final String identifier, final Map<String, Object> data) {
        if (Objects.isNull(this.config()) || this.isDisabled()) {
            return null;
        }

        final long expMs = this.getAesExpiredMs();
        final Map<String, Object> payload = new HashMap<>(4);
        payload.put(NAME_SUBJECT, identifier);
        payload.put(NAME_EXPIRE, expMs);
        if (data != null && !data.isEmpty()) {
            payload.put(NAME_ADDON_DATA, data);
        }

        try {
            final JObject jsonObj = SPI.J().put(payload);
            final byte[] rawBytes = jsonObj.encode().getBytes(StandardCharsets.UTF_8);
            final SecretKey key = this.deriveSecretKey(this.getAesSecret());
            final byte[] encryptedBytes = HED.encrypt(rawBytes, key, AES_SPEC);
            final String base64Str = Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
            return TOKEN_PREFIX + base64Str;
        } catch (final Exception e) {
            log.error("AES Token generation failed", e);
            throw new RuntimeException("Token generation failed", e);
        }
    }

    /**
     * ✅ [优化] 一次性验证并提取主题
     * <p>
     * 只执行一次解密。检查结构、完整性 (AES-GCM) 和过期时间。
     *
     * @param token AES 令牌
     * @return 如果有效且未过期，返回主题 (userId)；否则返回 null。
     */
    public String validateAndExtract(final String token) {
        // 1. 快速格式检查
        if (this.isValidFormat(token)) {
            return null;
        }

        try {
            // 2. 耗时操作：解密 (包括通过 GCM Tag 进行完整性检查)
            final Map<String, Object> payload = this.decryptTokenToMap(token);
            if (payload == null) {
                return null;
            }

            // 3. 验证：过期检查
            final Object expObj = payload.get(NAME_EXPIRE);
            if (expObj instanceof Number) {
                final long expireAt = ((Number) expObj).longValue();
                if (System.currentTimeMillis() > expireAt) {
                    return null; // 已过期
                }
            } else {
                return null; // 缺少过期字段 = 无效
            }

            // 4. 提取：返回主题
            return (String) payload.get(NAME_SUBJECT);

        } catch (final Exception e) {
            // 解密失败 (被篡改、错误的密钥或格式错误)
            return null;
        }
    }

    /**
     * 验证令牌（旧版/独立检查）
     */
    public boolean tokenValidate(final String token) {
        // 内部使用优化后的方法以避免代码重复，
        // 或者如果您需要不同的布尔逻辑，可以保持独立。
        // 用于严格的布尔检查：
        return this.validateAndExtract(token) != null;
    }

    /**
     * 提取主题（旧版/独立检查）
     */
    public String tokenSubject(final String token) {
        // 理想情况下，这也应该执行验证，否则可能会返回已过期的主题。
        // 但如果需要兼容性，保留原始逻辑（仅解密并获取）。
        if (this.isValidFormat(token)) {
            return null;
        }
        try {
            final Map<String, Object> payload = this.decryptTokenToMap(token);
            return payload == null ? null : (String) payload.get(NAME_SUBJECT);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * 提取扩展数据
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> tokenData(final String token) {
        if (this.isValidFormat(token)) {
            return Map.of();
        }
        try {
            final Map<String, Object> payload = this.decryptTokenToMap(token);
            return payload == null ? null : (Map<String, Object>) payload.get(NAME_ADDON_DATA);
        } catch (final Exception e) {
            return null;
        }
    }

    // ================== 私有辅助方法 ==================

    private boolean isValidFormat(final String token) {
        return this.isDisabled()
            || !StrUtil.isNotEmpty(token)
            || !token.startsWith(TOKEN_PREFIX);
    }

    private Map<String, Object> decryptTokenToMap(final String rawToken) throws Exception {
        final String base64Str = rawToken.substring(TOKEN_PREFIX.length());
        final byte[] encryptedBytes = Base64.getUrlDecoder().decode(base64Str);
        final SecretKey key = this.deriveSecretKey(this.getAesSecret());
        final byte[] plainBytes = HED.decrypt(encryptedBytes, key, AES_SPEC);
        final String jsonPayload = new String(plainBytes, StandardCharsets.UTF_8);
        final JObject parsed = JBase.parse(jsonPayload);
        return parsed.toMap();
    }

    private SecretKey deriveSecretKey(final String configSecret) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] keyBytes = digest.digest(configSecret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, "AES");
        } catch (final Exception e) {
            throw new RuntimeException("Failed to derive AES key", e);
        }
    }
}