package io.zerows.plugins.security;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import io.zerows.epoch.metadata.security.SecurityMeta;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handler 三合一的分流器
 * <pre>
 *     BEARER：JWT 和 AES
 *     BASIC： 基础认证
 *     其他：SPI 扩展
 * </pre>
 */
class AuthenticationHandlerGateway extends AuthenticationHandlerImpl<AuthenticationProvider> {
    private static final Cc<String, AuthenticationHandler> CC_HANDLER = Cc.openThread();
    private final ConcurrentMap<String, SecurityMeta> wallMap = new ConcurrentHashMap<>();
    private final Vertx vertx;
    private AuthenticationProvider provider;

    AuthenticationHandlerGateway(final Vertx vertx,
                                 final AuthenticationProvider provider, final Set<SecurityMeta> metaSet) {
        super(provider);
        this.vertx = vertx;
        metaSet.forEach(meta -> this.wallMap.put(meta.getType(), meta));
    }

    @Override
    public Future<User> authenticate(final RoutingContext context) {
        return null;
    }
}
