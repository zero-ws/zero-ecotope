package io.zerows.plugins.security;

import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.support.Ut;

public interface BackendProvider extends AuthenticationProvider {
    Cc<String, BackendProvider> CC_SKELETON = Cc.openThread();

    static BackendProvider of(final Vertx vertx, final SecurityMeta meta) {
        final String type = meta.getType().toUpperCase();
        final TokenType typeToken = Ut.toEnum(type, TokenType.class, null);
        final String cacheKey = vertx.hashCode() + "@" + type + "/" + meta.hashCode();
        if (TokenType.BASIC == typeToken) {
            return CC_SKELETON.pick(() -> new BackendProviderAnonymous(vertx, meta), cacheKey);
        } else {
            return CC_SKELETON.pick(() -> new BackendProviderLogged(vertx, meta), cacheKey);
        }
    }

    boolean support(TokenType type);
}
