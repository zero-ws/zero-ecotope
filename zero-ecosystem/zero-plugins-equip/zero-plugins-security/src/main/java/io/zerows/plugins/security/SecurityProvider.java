package io.zerows.plugins.security;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.spi.HPI;

/**
 * 安全扩展专用 SPI，用于创建 {@link AuthenticationHandler}
 *
 * @author lang : 2025-12-30
 */
@Deprecated
public interface SecurityProvider {
    Cc<String, SecurityProvider> CC_EXTENSION = Cc.open();

    Cc<String, AuthenticationProvider> CC_PROVIDER_401 = Cc.open();
    Cc<String, AuthenticationHandler> CC_HANDLER_401 = Cc.open();

    /**
     * 【核心修改1】定义哨兵对象 (Sentinel)。
     * 作用：用于在缓存中占位，表示“已查找过但未找到”。
     * 接口中的属性默认是 public static final 的，所以可以直接这样写。
     */
    SecurityProvider SENTINEL = new SecurityProvider() {
        @Override
        public AuthenticationHandler configureHandler401(final Vertx vertxRef, final SecurityConfig config,
                                                         final AuthenticationProvider authProvider) {
            return null;
        }

        @Override
        public AuthenticationProvider configureProvider401(final Vertx vertxRef, final SecurityConfig config) {
            return null;
        }
    };

    /**
     * {@link SPID} 中的值格式：Security/{securityType}，而且每种 SecurityType 对应唯一的实现类
     *
     * @param wallType 安全类型
     * @return 对应的安全扩展实现
     */
    static SecurityProvider of(final String wallType) {
        // 【核心修改2】使用哨兵模式
        final SecurityProvider cached = CC_EXTENSION.pick(() -> {
            // 1. 查找 SPI
            final SecurityProvider found = HPI.findOne(SecurityProvider.class, "Security/" + wallType);

            // 2. 如果没找到，返回哨兵，而不是 null
            // (因为 Cc/ConcurrentHashMap 不支持存 null，存 null 会导致下次还去查，导致日志刷屏)
            return found == null ? SENTINEL : found;
        }, wallType);

        // 3. 对外转换：如果是哨兵，说明其实是没找到，返回 null 给调用者
        return cached == SENTINEL ? null : cached;
    }

    AuthenticationHandler configureHandler401(Vertx vertxRef, SecurityConfig config,
                                              AuthenticationProvider authProvider);

    AuthenticationProvider configureProvider401(Vertx vertxRef, SecurityConfig config);
}