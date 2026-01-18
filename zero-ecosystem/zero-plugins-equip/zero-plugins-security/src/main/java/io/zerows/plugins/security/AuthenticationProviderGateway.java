package io.zerows.plugins.security;

import io.r2mo.jaas.token.TokenType;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.Credentials;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.security.exception._80248Exception404ProviderMiss;
import io.zerows.plugins.security.exception._80249Exception409ProviderType;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 内部认证提供者专用网关
 */
public class AuthenticationProviderGateway implements AuthenticationProvider {
    private final ConcurrentMap<String, BackendProvider> providerMap = new ConcurrentHashMap<>();
    private final Vertx vertx;

    AuthenticationProviderGateway(final Vertx vertx, final Set<SecurityMeta> metaSet) {
        this.vertx = vertx;
        metaSet.forEach(meta -> {
            final BackendProvider provider = BackendProvider.of(vertx, meta);
            final String type = meta.getType().toUpperCase();
            this.providerMap.putIfAbsent(type, provider);
        });
    }

    protected Vertx vertx() {
        return this.vertx;
    }

    @Override
    public Future<User> authenticate(final Credentials credentials) {
        final String authorization = credentials.toHttpAuthorization();
        final TokenType type = TokenType.fromString(authorization);
        final String typeStr = type.name();
        final BackendProvider found = this.providerMap.get(typeStr);


        // 无法找到 BackendProvider
        if (Objects.isNull(found)) {
            return Future.failedFuture(new _80248Exception404ProviderMiss(typeStr));
        }


        // BackendProvider 不支持该类型
        if (!found.support(type)) {
            final String provider = found.getClass().getName();
            return Future.failedFuture(new _80249Exception409ProviderType(provider, typeStr));
        }


        return found.authenticate(credentials);
    }
}
