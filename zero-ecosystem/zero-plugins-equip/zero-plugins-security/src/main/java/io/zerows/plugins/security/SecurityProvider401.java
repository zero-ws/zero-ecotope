package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author lang : 2025-10-29
 */
class SecurityProvider401 extends SecurityProviderBase implements AuthenticationProvider {

    SecurityProvider401(final Vertx vertxRef, final SecurityMeta meta) {
        super(vertxRef, meta);
    }

    @Override
    public Future<User> authenticate(final Credentials credentials) {
        return null;
    }
}
