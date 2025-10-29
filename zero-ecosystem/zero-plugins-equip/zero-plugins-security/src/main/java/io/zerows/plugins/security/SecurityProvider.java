package io.zerows.plugins.security;

import io.vertx.core.Vertx;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author lang : 2025-10-29
 */
class SecurityProvider {
    private final static SecurityManager MANAGER = SecurityManager.of();
    private final SecurityMeta meta;
    private final SecurityConfig config;

    public SecurityProvider(final Vertx vertxRef, final SecurityMeta meta) {
        this.meta = meta;
        this.config = MANAGER.configOf(meta.getType());
    }
}
