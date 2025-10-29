package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.ChainAuthHandler;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallProvider;

import java.util.Set;

/**
 * 基础的 Security Wall 提供者，用于处理 401 和 403 错误的认证和授权。
 *
 * @author lang : 2025-10-29
 */
public class SecurityWallProvider implements WallProvider {
    private static final Cc<String, AuthenticationProvider> CC_PROVIDER_401 = Cc.openThread();

    @Override
    public AuthenticationProvider provider401(final Vertx vertxRef, final SecurityMeta meta) {
        final String cacheKey = vertxRef.hashCode() + "@" + meta.hashCode();
        return CC_PROVIDER_401.pick(() -> new SecurityProvider401(vertxRef, meta), cacheKey);
    }

    @Override
    public ChainAuthHandler handler401(final Vertx vertxRef, final Set<SecurityMeta> metaSet) {
        return null;
    }

    @Override
    public AuthorizationHandler handler403(final Vertx vertxRef, final Set<SecurityMeta> metaSet) {
        return null;
    }
}
