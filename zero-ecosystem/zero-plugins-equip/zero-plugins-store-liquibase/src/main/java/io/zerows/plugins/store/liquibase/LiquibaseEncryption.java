package io.zerows.plugins.store.liquibase;

import io.r2mo.base.secure.EDCrypto;
import io.zerows.epoch.constant.KName;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class LiquibaseEncryption extends Properties {

    private static final EDCrypto CRYPTO = new LiquibaseEDCrypto();

    /**
     * RSA 密文通常较长（Base64 后 2048 位密钥约 344~392 字符）。
     * 设置一个保守阈值：>=128 即视为“可能是密文”。
     */
    private static final int ENC_MIN_LEN = 128;

    /** 粗略 Base64 检测（只做快速过滤，不做严格校验） */
    private static final Pattern BASE64ish = Pattern.compile("^[A-Za-z0-9+/=\\r\\n]+$");

    public LiquibaseEncryption() {
        super.defaults = new Properties();
    }

    @Override
    public synchronized Object put(final Object paramK, final Object paramV) {
        if (!KName.PASSWORD.equals(paramK)) {
            return super.defaults.put(paramK, paramV);
        }

        final String raw = paramV == null ? null : String.valueOf(paramV).trim();
        if (raw == null || raw.isEmpty()) {
            // 空密码，直接写入
            return super.defaults.put(paramK, raw);
        }

        // 按“长度 + 形态”粗判是否为密文（RSA 私钥加密 -> Base64 长串）
        final boolean looksEncrypted = raw.length() >= ENC_MIN_LEN && BASE64ish.matcher(raw).matches();

        if (!looksEncrypted) {
            // 认为是明文，直接写入
            return super.defaults.put(paramK, raw);
        }

        // 认为是密文，尝试解密；失败则回退原值并告警
        try {
            final String pwd = CRYPTO.decrypt(raw);
            if (pwd != null) {
                return super.defaults.put(paramK, pwd);
            } else {
                log.warn("[ ZERO ] 解密返回 null，回退为原始值（已按密文判断）：len={}", raw.length());
                return super.defaults.put(paramK, raw);
            }
        } catch (final Throwable ex) {
            log.warn("[ ZERO ] 密码解密失败，回退原始值（可能并非密文）：len={}, err={}", raw.length(), ex.toString());
            return super.defaults.put(paramK, raw);
        }
    }
}
