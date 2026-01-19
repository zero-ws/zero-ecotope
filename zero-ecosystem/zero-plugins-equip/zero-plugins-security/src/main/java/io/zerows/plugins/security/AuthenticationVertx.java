package io.zerows.plugins.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 内部 Provider 专用构造器，用于构造内部认证对应的 Provider 组件
 *
 * @author lang : 2025-10-29
 */
@Slf4j
class AuthenticationVertx {
    private static final AtomicBoolean IS_OUT = new AtomicBoolean(Boolean.TRUE);

    /**
     * 注意配置提取流程，此处配置提取流程主要是依赖 Provider / Consumer 的流程信息来处理
     * <pre>
     *     1. {@link SecurityManager} 中包含了如下信息
     *        vertx = Map<SecurityType, SecurityConfig>
     *        appId / appName = Map<SecurityType, SecurityConfig>
     *        所以提取过程中使用 {@link Vertx} 实例的 hashCode 做为第一优先级的配置提取
     *
     *     2. 根据 {@link Vertx} 实例的 hashCode 可提取所有 SecurityType = SecurityConfig 的 Map，然后根据
     *        {@link SecurityMeta#getType()} 提取配置构造内部 Provider
     * </pre>
     * 在当前环境中，映射信息如下：
     * <pre>
     *     1. {@link Vertx} 实例 x 1
     *        - configOf(SecurityType)
     *        - configOf(SecurityType, Vertx)
     *        上述两个方法等价
     *
     *     2. {@link Vertx} 实例 x N
     *        - configOf(SecurityType)              --> 默认取第一个 Vertx 实例的配置
     *        - configOf(SecurityType, Vertx)       --> 根据 Vertx 实例提取对应配置
     * </pre>
     *
     * @param vertxRef Vertx 实例
     * @param meta     安全元信息
     * @return 认证提供者
     */
    @SuppressWarnings("unchecked")
    static <T extends AuthenticationProvider> T createProvider(final Vertx vertxRef, final SecurityMeta meta) {
        final SecurityConfig config = SecurityManager.of().configOf(meta.getType(), vertxRef);
        final SecurityProvider provider = providerOf(vertxRef, meta);
        return Objects.isNull(provider) ? null : (T) provider.configureProvider401(vertxRef, config);
    }

    static AuthenticationHandler createHandler(final Vertx vertxRef, final SecurityMeta meta,
                                               final AuthenticationProvider providerAuth) {
        final SecurityConfig config = SecurityManager.of().configOf(meta.getType(), vertxRef);
        final SecurityProvider provider = providerOf(vertxRef, meta);
        return Objects.isNull(provider) ? null : provider.configureHandler401(vertxRef, config, providerAuth);
    }

    private static SecurityProvider providerOf(final Vertx vertxRef, final SecurityMeta meta) {
        final SecurityConfig config = SecurityManager.of().configOf(meta.getType(), vertxRef);
        if (Objects.isNull(config)) {
            if (IS_OUT.getAndSet(Boolean.FALSE)) {
                log.warn("[ PLUG ] ( Secure ) vertx.yml 配置缺失 / type = {}, 无法构造 SecurityProvider", meta.getType());
            }
            return null;
        }
        final SecurityProvider provider = SecurityProvider.of(config.type());
        if (Objects.isNull(provider)) {
            if (IS_OUT.getAndSet(Boolean.FALSE)) {
                log.warn("[ PLUG ] ( Secure ) 未找到对应的 SecurityProvider / type = {}", config.type());
            }
            return null;
        }
        return provider;
    }
}
