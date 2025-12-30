package io.zerows.plugins.security;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.platform.enums.SecurityType;
import io.zerows.spi.HPI;

/**
 * 安全扩展专用 SPI，用于创建 {@link AuthenticationHandler}
 *
 * @author lang : 2025-12-30
 */
public interface SecurityProvider {
    Cc<SecurityType, SecurityProvider> CC_EXTENSION = Cc.open();

    Cc<String, AuthenticationProvider> CC_PROVIDER_401 = Cc.open();
    Cc<String, AuthenticationHandler> CC_HANDLER_401 = Cc.open();

    /**
     * {@link SPID} 中的值格式：Security/{securityType}，而且每种 SecurityType 对应唯一的实现类
     *
     * @param securityType 安全类型
     *
     * @return 对应的安全扩展实现
     */
    static SecurityProvider of(final SecurityType securityType) {
        return CC_EXTENSION.pick(() ->
            HPI.findOne(SecurityProvider.class, "Security/" + securityType.name()), securityType);
    }

    AuthenticationHandler configureHandler401(Vertx vertxRef, SecurityConfig config);

    AuthenticationProvider configureProvider401(Vertx vertxRef, SecurityConfig config);
}
