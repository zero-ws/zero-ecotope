package io.zerows.plugins.security;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.epoch.web.Account;
import io.zerows.plugins.security.exception._80243Exception401NeedLogin;

import java.util.Objects;

/**
 * @author lang : 2025-10-30
 */
class AuthenticationHandlerOne extends AuthenticationHandlerImpl<AuthenticationProvider> {
    private final SecurityMeta meta;

    AuthenticationHandlerOne(final AuthenticationProvider provider,
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
            final HttpServerRequest request = context.request();
            final String requestUri = request.method().name() + " " + request.uri();
            final WebException error = new _80243Exception401NeedLogin(requestUri);
            return Future.failedFuture(error);
        }


        /*
         * 之前的 Handler 已经创建好了 User 对象，证明此处之前的认证是成功的，接下来只需要调用的实际是
         * WallExecutor 内置的授权方法来进行
         * - 授权
         * - 签名
         * - 分发即可
         * 所以此处暂时保留，若后续需要在 Handler 中进行额外的处理，可以在此处添加逻辑
         */
        final User logged = Account.userVx(user, context.session());
        return Future.succeededFuture(logged);
    }
}
