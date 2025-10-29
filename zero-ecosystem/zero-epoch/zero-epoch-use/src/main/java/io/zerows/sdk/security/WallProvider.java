package io.zerows.sdk.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author lang : 2025-10-29
 */
public interface WallProvider {

    AuthenticationProvider provider401(Vertx vertxRef, SecurityMeta meta);

    AuthorizationProvider provider403(Vertx vertxRef, SecurityMeta meta);

    interface BuiltIn {
        AuthenticationProvider provider401(Vertx vertxRef, SecurityMeta meta);

        AuthorizationProvider provider403(Vertx vertxRef, SecurityMeta meta);
    }
}
