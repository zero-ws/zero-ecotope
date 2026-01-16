package io.zerows.plugins.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallHandler;
import io.zerows.sdk.security.WallProvider;

import java.util.Set;

/**
 * 基础的 Security Wall 提供者，用于处理 401 和 403 错误的认证和授权。
 *
 * @author lang : 2025-10-29
 */
public class SecurityWallProvider implements WallProvider {

    @Override
    public AuthenticationProvider providerOfAuthentication(final Vertx vertxRef, final Set<SecurityMeta> metaSet) {
        return SecurityProviderFactory.of(vertxRef).providerOfAuthentication(metaSet);
    }

    @Override
    public WallHandler handlerOfAuthentication(final Vertx vertxRef, final Set<SecurityMeta> metaSet) {
        return SecurityProviderFactory.of(vertxRef).handlerOfAuthentication(metaSet);
    }

    @Override
    public AuthorizationHandler handlerOfAuthorization(final Vertx vertxRef, final Set<SecurityMeta> metaSet) {
        return SecurityProviderFactory.of(vertxRef).handlerOfAuthorization(metaSet);
    }
}
