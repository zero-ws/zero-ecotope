package io.zerows.platform.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 🔒 安全类型（WallType）
 * 安全墙的类型和认证方式、授权模式会有一定的区别
 * <pre>
 *     1. NONE
 *        - NONE - 表示无 Handler 的 Token 认证模式，但 Provider 依旧存在，比如典型的LDAP Provider，这种模式下的认证完全依赖 Provider 来完成，
 *          Handler 什么都不用做，真正 Handler 校验时只校验 Token的合法性即可。
 *
 *     2. BASIC 模式
 *        - BASIC 普通模式：这种模式发送的就是 BASIC {TOKEN} 的格式，此处的 TOKEN 就是 Base64 编码后的 用户名:密码 字符串，Handler 负责解码和校验
 *          Provider 负责加载用户信息进行二次校验，但是这种模式不在服务端生成会话，为一次性认证。且目的就是为了保证每次都进行特殊认证，性能较差，另外一个
 *          目的也是让用户不用频繁发送用户名和密码，避免明文传输，而且 BASIC 普通模式不需要登录。
 *
 *        - AES 兜底加密：这种模式下的 BASIC {TOKEN} 中的 TOKEN 部分不是 Base64 编码的用户名:密码/字符串，而是经过 AES 加密后的字符串，Handler 负
 *          责解密和校验，Provider 负责加载用户信息进行二次校验，这种模式是标准的非 JWT 模式，会在服务端生成会话，下次再次请求时可以直接验 Token 即可，
 *          性能较好。
 *
 *     3. JWT 模式
 *        - JWT 模式下，执行 Handler 需要通过 SPI 的方式接入，必须在 Maven 中追加 JWT 相关依赖才可生效，如果作为核心模式会在部分场景下无效，但底层可
 *          使用官方的 vertx-auth-jwt 组件进行处理.
 *
 *     4. EXTENSION 模式
 *        - EXTENSION 模式下，Handler 和 Provider 都需要通过 SPI 的方式接入，用户可以自行实现认证和授权的逻辑，系统只负责调用。
 * </pre>
 *
 * <p>用于标识系统采用的认证 / 鉴权“墙”的类型。</p>
 */
public enum WallType {
    NONE("none"),                                    // 🚪 无认证（开放访问），也可能是 Handler 中不需要，登录中所需
    BASIC("basic"),                                  // 🔐 基本认证（Basic Auth）
    JWT("jwt"),                                      // 🪪 基于 JWT 的无状态认证
    EXTENSION("extension");

    private static final ConcurrentMap<String, WallType> TYPE_MAP = new ConcurrentHashMap<>();

    static {
        Arrays.stream(WallType.values()).forEach(wall -> TYPE_MAP.put(wall.key(), wall));
    }

    /**
     * 🗝️ 对应配置中的键名（config key）
     */
    private transient final String configKey;

    WallType(final String configKey) {
        this.configKey = configKey;
    }

    /**
     * 🔎 根据配置键名解析枚举
     *
     * @param configKey 配置中的键名
     * @return 命中的 SecurityType；未命中则返回 null
     */
    public static WallType from(final String configKey) {
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
