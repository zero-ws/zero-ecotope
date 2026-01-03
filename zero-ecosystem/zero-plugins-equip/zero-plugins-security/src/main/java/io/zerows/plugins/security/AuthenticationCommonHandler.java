package io.zerows.plugins.security;

import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallExecutor;

import java.util.Objects;

/**
 * @author lang : 2025-10-30
 */
class AuthenticationCommonHandler extends AuthenticationHandlerImpl<AuthenticationProvider> {
    private final SecurityMeta meta;

    AuthenticationCommonHandler(final AuthenticationProvider provider,
                                final SecurityMeta meta) {
        super(provider);
        this.meta = meta;
    }

    @Override
    public Future<User> authenticate(final RoutingContext context) {
        /*
         * 当前 Handler 不是第一个，而是继续验证，所以此处直接获取 User 即可，若没有 User
         * 则表示之前的认证失败
         */
        final User user = context.user();
        if (Objects.isNull(user)) {
            final WebException error = new _401UnauthorizedException("[ PLUG ] 权限不够！");
            return Future.failedFuture(error);
        }


        final WallExecutor executor = this.meta.getProxy();
        if (Objects.isNull(executor)) {
            return Future.failedFuture(new _401UnauthorizedException("[ PLUG ] 认证执行器未找到！"));
        }


        // 可在认证过程中填充额外的信息
        final SecurityCredentials credentials = new SecurityCredentials(this.meta, user);
        return this.authProvider.authenticate(credentials);
    }
}
