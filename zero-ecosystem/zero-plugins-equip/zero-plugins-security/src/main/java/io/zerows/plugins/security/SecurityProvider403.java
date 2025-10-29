package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author lang : 2025-10-29
 */
class SecurityProvider403 extends SecurityProvider implements AuthorizationProvider {

    public SecurityProvider403(final Vertx vertxRef, final SecurityMeta meta) {
        super(vertxRef, meta);
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public Future<Void> getAuthorizations(final User user) {
        return null;
    }
}
