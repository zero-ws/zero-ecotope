package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;

import java.util.Objects;

/**
 * @author lang : 2025-10-29
 */
class SecurityChain {
    private static final Cc<String, SecurityChain> CC_CHAIN = Cc.openThread();
    private static final Cc<String, AuthenticationProvider> CC_PROVIDER_401 = Cc.openThread();
    private final Vertx vertxRef;

    private SecurityChain(final Vertx vertxRef) {
        this.vertxRef = vertxRef;
    }

    static SecurityChain of(final Vertx vertxRef) {
        final String cacheKey = Objects.toString(vertxRef.hashCode());
        return CC_CHAIN.pick(() -> new SecurityChain(vertxRef), cacheKey);
    }

    AuthenticationProvider provider401(final SecurityMeta meta) {
        return null;
    }
}
