package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.platform.enums.SecurityType;
import io.zerows.platform.management.StoreApp;
import io.zerows.specification.app.HApp;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 安全管理器
 * <pre>
 *     1. 按类型管理配置相关信息（可支持应用级配置管理）
 *     2. 提供统一的安全接口，扫描系统中的 {@link Wall} 来实现 {@link SecurityMeta} 的构造流程，最终路由依靠此处
 *        的 {@link SecurityMeta} 实现认证授权的注入。
 * </pre>
 * 关于内置和外置两层安全体系的设计思路：
 * <pre>
 *     1. 凭证的提取：Vert.x 内置的认证授权体系
 *        - 内置体系主要负责凭证的提取和初步的认证授权
 *        - 通过配置不同的认证提供者（如 JWT、OAuth2 等），实现对不同类型凭证的支持
 *     2. 业务逻辑的安全处理：应用层的安全管理器
 *        - 外置体系主要负责业务逻辑的安全处理
 *        - 通过扫描应用中的安全注解（如 {@link Wall}），构建应用级的安全上下文（如 {@link SecurityMeta}）
 * </pre>
 *
 * @author lang : 2025-10-29
 */
@Slf4j
class SecurityManager {
    /**
     * 此处 CONFIG_MAP 结构是双模型
     * <pre>
     *     key = appId / appName 或 vertx hashCode
     *     1. 默认单实例场景下，vertx hashCode
     *        vertx:
     *          security:
     *            ????
     *     2. 非持久化场景：App Name
     *        app:
     *          security:
     *            ????
     *     3. 持久化场景：App Id
     * </pre>
     */
    private static final Cc<String, Map<SecurityType, SecurityConfig>> CONFIG_MAP = Cc.open();

    private static final Map<SecurityType, SecurityConfig> CONFIG_DEFAULT = new HashMap<>();

    private static final SecurityManager INSTANCE = new SecurityManager();


    private SecurityManager() {
    }

    public static SecurityManager of() {
        return INSTANCE;
    }

    void registryOf(final HConfig config, final String appId) {
        Objects.requireNonNull(config, "[ PLUG ] 安全配置对象不能为空！");

        final HApp stored = StoreApp.of().valueGet(appId);
        if (Objects.isNull(stored)) {
            log.warn("[ PLUG ] ( Secure ) 应用 `{}` 未找到，无法注册安全配置！", appId);
            return;
        }

        final String cacheKey = stored.isLoad() ? stored.id() : stored.name();
        CONFIG_MAP.pick(() -> this.createConfiguration(config, cacheKey), cacheKey);

    }

    void registryOf(final HConfig config, final int vertxCode) {
        final Map<SecurityType, SecurityConfig> defaultMap = CONFIG_MAP.pick(
            () -> this.createConfiguration(config, vertxCode), String.valueOf(vertxCode));
        // 默认配置只会被初始化一次，为当前启动节点（或主节点）的全局默认配置
        if (CONFIG_DEFAULT.isEmpty()) {
            CONFIG_DEFAULT.putAll(defaultMap);
        }
    }

    private Map<SecurityType, SecurityConfig> createConfiguration(
        final HConfig config, final Object ownerId
    ) {
        final Map<SecurityType, SecurityConfig> securityMap = new HashMap<>();
        final JsonObject options = config.options();
        // 迭代目前可配的所有安全类型
        for (final String optionKey : options.fieldNames()) {
            final SecurityType type = SecurityType.from(optionKey);
            if (Objects.isNull(type)) {
                continue;
            }

            final JsonObject configOption = options.getJsonObject(optionKey);
            final SecurityConfig configObj = new SecurityConfig(type, configOption);
            securityMap.put(type, configObj);
            log.info("[ PLUG ] ( Secure ) By - {} / 已注册安全配置：`{}`, value = {}", ownerId, configObj.key(), configOption.encode());
        }
        return securityMap;
    }

    SecurityConfig configJwt() {
        return CONFIG_DEFAULT.getOrDefault(SecurityType.JWT, null);
    }

    SecurityConfig configOf(final SecurityType type) {
        return CONFIG_DEFAULT.getOrDefault(type, null);
    }

    SecurityConfig configOf(final SecurityType type, final Vertx vertxRef) {
        return CONFIG_MAP.getOrDefault(String.valueOf(vertxRef.hashCode()), Map.of())
            .getOrDefault(type, null);
    }

    SecurityConfig configJwt(final String appOr) {
        final Map<SecurityType, SecurityConfig> configMap = CONFIG_MAP.getOrDefault(appOr, new HashMap<>());
        return configMap.getOrDefault(SecurityType.JWT, null);
    }
}
