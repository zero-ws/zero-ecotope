package io.zerows.plugins.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallProvider;

/**
 * @author lang : 2025-10-29
 */
public class SecurityWallProvider implements WallProvider {

    @Override
    public AuthenticationProvider provider401(final Vertx vertxRef, final SecurityMeta meta) {
        return null;
    }

    @Override
    public AuthorizationProvider provider403(final Vertx vertxRef, final SecurityMeta meta) {
        return null;
    }
}
