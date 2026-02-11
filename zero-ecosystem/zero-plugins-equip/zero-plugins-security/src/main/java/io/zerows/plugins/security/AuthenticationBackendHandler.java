package io.zerows.plugins.security;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._403ForbiddenException;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.epoch.web.Account;
import io.zerows.plugins.security.exception._80243Exception401NeedLogin;
import io.zerows.plugins.security.service.AsyncAuthorization;

import java.util.Objects;

/**
 * @author lang : 2025-10-30
 */
class AuthenticationBackendHandler extends AuthenticationHandlerImpl<AuthenticationProvider> {
    private static final Cc<String, AuthenticationBackendHandler> CC_HANDLER = Cc.openThread();
    private static final Cc<String, AuthorizationProvider> CC_AUTHORIZATION = Cc.openThread();
    private final SecurityMeta meta;

    private AuthenticationBackendHandler(final AuthenticationProvider provider,
                                         final SecurityMeta meta) {
        super(provider);
        this.meta = meta;
    }

    static AuthenticationBackendHandler of(final AuthenticationProvider provider,
                                           final SecurityMeta meta) {
        final String cacheKey = String.valueOf(System.identityHashCode(meta));
        return CC_HANDLER.pick(() -> new AuthenticationBackendHandler(provider, meta), cacheKey);
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


        final SecuritySession session = SecuritySession.of();
        if (session.isDisabled403()) {
            // 授权没有开启，直接放行
            return Future.succeededFuture(logged);
        }


        // ----------------------- 开启授权后执行处理 -----------------------
        final HttpServerRequest request = context.request();
        final String appId = request.getHeader(KWeb.HEADER.X_APP_ID);
        final AsyncAuthorization authorization =
            StrUtil.isEmpty(appId) ? AsyncAuthorization.of() : AsyncAuthorization.of(appId);
        if (Objects.isNull(authorization)) {
            // 授权组件未找到，直接放行
            return Future.succeededFuture(logged);
        }

        
        // 自动开启缓存，只有第一次会查询
        return authorization.seekProfile(user).compose(authorized -> {
            if (Objects.isNull(authorized)) {
                // 用户信息查询失败，拒绝
                final String requestUri = request.method().name() + " " + request.uri();
                return Future.failedFuture(new _403ForbiddenException("[ PLUG ] 用户信息查询失败，拒绝访问！api = " + requestUri));
            }



            /*
             * 此时才是真正要加载 AuthorizationProvider 的时候了，而且此处必须执行权限加载
             * - 填充 User 的 authorizations 相关信息
             * - 授权完成之后，执行 setUser 方法，填充 RoutingContext 的 User 信息
             */
            final AuthorizationProvider authorizationProvider = CC_AUTHORIZATION.pick(() -> new AuthorizationProviderOne(this.meta));
            return authorizationProvider.getAuthorizations(logged)
                .map(done -> session.authorizedUser(context, logged));
        });

    }
}
