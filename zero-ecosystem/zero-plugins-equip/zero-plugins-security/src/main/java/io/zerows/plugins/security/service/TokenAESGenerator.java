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
 * AES Symmetric Encryption Token Generator (HED Static Implementation)
 * <p>
 * Features:
 * 1. Uses {@link HED} static methods for encryption/decryption.
 * 2. Implements AES-256 (GCM mode) via {@link LicSym.AlgLicenseAes#AES_256}.
 * 3. Uses {@link SecurityConfig} for silent configuration.
 * 4. Manages Key Derivation (String -> SecretKey) and Base64 encoding manually to adapt to HED API.
 *
 * @author lang
 */
@Slf4j
public class TokenAESGenerator {

    // ================== Constants ==================

    // Physical Fingerprint Prefix: r2a_
    public static final String TOKEN_PREFIX = "r2a_";
    private static final String NAME_SUBJECT = "sub";
    private static final String NAME_EXPIRE = "exp"; // Timestamp in ms
    private static final String NAME_ADDON_DATA = "ext";
    // Algorithm Spec: AES-256 (Corresponds to GCM mode in HED implementation)
    private static final LicSym.AlgLicenseAes AES_SPEC = LicSym.AlgLicenseAes.AES_256;

    // ================== Dependencies ==================\
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
     * Generate AES Token
     */
    public String tokenGenerate(final String identifier, final Map<String, Object> data) {
        // 1. Check enabled status
        if (Objects.isNull(this.config()) || this.isDisabled()) {
            return null;
        }

        // 2. Assemble Payload
        final long expMs = this.getAesExpiredMs();
        final Map<String, Object> payload = new HashMap<>(4);
        payload.put(NAME_SUBJECT, identifier);
        payload.put(NAME_EXPIRE, expMs);
        if (data != null && !data.isEmpty()) {
            payload.put(NAME_ADDON_DATA, data);
        }

        try {
            // 3. Serialize (Map -> JSON String -> byte[])
            final JObject jsonObj = SPI.J().put(payload);
            final byte[] rawBytes = jsonObj.encode().getBytes(StandardCharsets.UTF_8);

            // 4. Derive Key (String -> SecretKey)
            // HED requires a SecretKey object, so we must convert the config string
            final SecretKey key = this.deriveSecretKey(this.getAesSecret());

            // 5. HED Encrypt (byte[] -> byte[])
            // Calls HEDBase.encrypt(byte[], SecretKey, AlgLicenseSpec)
            final byte[] encryptedBytes = HED.encrypt(rawBytes, key, AES_SPEC);

            // 6. Encode (byte[] -> URL Safe Base64 String)
            final String base64Str = Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);

            return TOKEN_PREFIX + base64Str;

        } catch (final Exception e) {
            log.error("AES Token generation failed", e);
            throw new RuntimeException("Token generation failed", e);
        }
    }

    /**
     * Validate Token
     */
    public boolean tokenValidate(final String token) {
        if (this.isValidFormat(token)) {
            return false;
        }
        try {
            final Map<String, Object> payload = this.decryptTokenToMap(token);
            if (payload == null) {
                return false;
            }
            // Check Expiration
            final Object expObj = payload.get(NAME_EXPIRE);
            if (expObj instanceof Number) {
                return System.currentTimeMillis() <= ((Number) expObj).longValue();
            }
            return false;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Extract Subject
     */
    public String tokenSubject(final String token) {
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
     * Extract Extended Data
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

    // ================== Private Helper Methods ==================

    private boolean isValidFormat(final String token) {
        return this.isDisabled()
            || !StrUtil.isNotEmpty(token)
            || !token.startsWith(TOKEN_PREFIX);
    }

    private Map<String, Object> decryptTokenToMap(final String rawToken) throws Exception {
        // 1. Remove Prefix
        final String base64Str = rawToken.substring(TOKEN_PREFIX.length());

        // 2. Decode (URL Safe Base64 String -> byte[])
        final byte[] encryptedBytes = Base64.getUrlDecoder().decode(base64Str);

        // 3. Derive Key
        final SecretKey key = this.deriveSecretKey(this.getAesSecret());

        // 4. HED Decrypt (byte[] -> byte[])
        // Calls HEDBase.decrypt(byte[], SecretKey, AlgLicenseSpec)
        final byte[] plainBytes = HED.decrypt(encryptedBytes, key, AES_SPEC);

        // 5. Deserialize
        final String jsonPayload = new String(plainBytes, StandardCharsets.UTF_8);
        final JObject parsed = JBase.parse(jsonPayload);
        return parsed.toMap();
    }

    /**
     * Key Derivation Function (KDF)
     * Converts arbitrary configuration string to AES-256 compliant 32-byte Key
     */
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