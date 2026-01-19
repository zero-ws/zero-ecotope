package io.zerows.plugins.security;

import io.r2mo.jaas.token.TokenType;
import io.r2mo.typed.exception.web._401UnauthorizedException;
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
        // Fix: AES 类型的 Provider 无法找到的问题
        if (!this.providerMap.containsKey(TokenType.AES.name())) {
            final BackendProvider provider = this.providerMap.values().stream()
                .filter(each -> each instanceof BackendProviderLogged)
                .findAny().orElse(null);
            // 兼容使用 AES 认证
            if (Objects.nonNull(provider)) {
                this.providerMap.put(TokenType.AES.name(), provider);
            }
        }
    }

    protected Vertx vertx() {
        return this.vertx;
    }

    @Override
    public Future<User> authenticate(final Credentials credentials) {
        final String authorization = credentials.toHttpAuthorization();
        // 如果依然是 null (比如构造错误的 AsyncSession)，直接报错
        if (authorization == null) {
            return Future.failedFuture(new _401UnauthorizedException("无法生成有效的认证头，凭证无效！"));
        }
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
