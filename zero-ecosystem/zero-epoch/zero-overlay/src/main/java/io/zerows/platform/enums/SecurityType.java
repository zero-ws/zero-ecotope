package io.zerows.platform.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 🔒 安全类型（SecurityType）
 *
 * <p>用于标识系统采用的认证 / 鉴权“墙”的类型。</p>
 */
public enum SecurityType {
    BASIC("basic"),        // 🔐 基本认证（Basic Auth）
    JWT("jwt"),            // 🪪 基于 JWT 的无状态认证
    LDAP("ldap"),          // 🗂️ LDAP 目录认证
    OTP("otp"),            // ⏱️ 一次性口令（TOTP/HOTP）
    /*
     * 🧱 Zero 框架内置模式
     * 🔑 与配置文件中的 `rules` 键关联
     * 📚 遵循 Vert.x 原生标准（以下取自官方指南）
     *
     * 🧩 在 `provider/handler` 目录下提供了若干可复用模板
     */
    NONE("none"),          // 🚪 无认证（开放访问）
    OAUTH2("oauth2"),      // 🌐 OAuth2 / OIDC 认证
    ABAC("abac"),          // 📏 基于属性的访问控制（ABAC）
    HT_PASSWD("htpasswd"), // 📄 Apache htpasswd 文件认证
    HT_DIGEST("htdigest"), // 📑 Apache htdigest 摘要认证

    /*
     * 🧩 选择该类型表示使用 Zero 扩展类认证墙（而非 Vert.x 原生）
     * 📝 若传入类型不在上述集合内，需要提供自定义 key
     * 📄 该 key 用于从 `vertx-secure.yml` 等外部配置加载认证信息
     */
    EXTENSION("extension");

    private static final ConcurrentMap<String, SecurityType> TYPE_MAP = new ConcurrentHashMap<>();

    static {
        Arrays.stream(SecurityType.values()).forEach(wall -> TYPE_MAP.put(wall.key(), wall));
    }

    /**
     * 🗝️ 对应配置中的键名（config key）
     */
    private transient final String configKey;

    SecurityType(final String configKey) {
        this.configKey = configKey;
    }

    /**
     * 🔎 根据配置键名解析枚举
     *
     * @param configKey 配置中的键名
     * @return 命中的 SecurityType；未命中则返回 null
     */
    public static SecurityType from(final String configKey) {
        return TYPE_MAP.getOrDefault(configKey, null);
    }

    /**
     * 🧰 获取所有可用类型的键名集合
     *
     * @return 键名集合
     */
    public static Set<String> keys() {
        return TYPE_MAP.keySet();
    }

    /**
     * 🔑 获取当前枚举对应的配置键名
     *
     * @return 配置键名
     */
    public String key() {
        return this.configKey;
    }
}
