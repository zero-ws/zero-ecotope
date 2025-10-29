package io.zerows.plugins.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.zerows.epoch.metadata.security.SecurityConfig;

/**
 * @author lang : 2025-10-29
 */
public interface BuiltInProvider {

    AuthenticationProvider createProvider(Vertx vertx, SecurityConfig config);
}
