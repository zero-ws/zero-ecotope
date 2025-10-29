package io.zerows.plugins.security;

import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import io.vertx.ext.web.handler.impl.HTTPAuthorizationHandler;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.plugins.security.authenticate.AuthenticateBuiltInProvider;
import io.zerows.plugins.security.authenticate.ChainHandler;
import io.zerows.sdk.security.OldLeeBuiltIn;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Deprecated
@SuppressWarnings("all")
public abstract class AbstractOldLee implements OldLeeBuiltIn {

    // --------------------------- Interface Method

    @Override
    public AuthorizationHandler authorization(final Vertx vertx, final SecurityMeta config) {
        //        final Class<?> handlerCls = config.getHandler();
        //        if (Objects.isNull(handlerCls)) {
        //            // Default profile is no access ( 403 )
        //            final AuthorizationHandler handler = AuthorizationBuiltInHandler.create(config);
        //            final AuthorizationProvider provider = AuthorizationBuiltInProvider.provider(config);
        //            handler.addAuthorizationProvider(provider);
        //
        //            /*
        //             * Check whether user defined provider, here are defined provider
        //             * for current 403 workflow instead of standard workflow here
        //             */
        //            final KSecurity.Provider item = config.item();
        //            final Class<?> providerCls = item.getProviderAuthenticate();
        //            if (Objects.nonNull(providerCls)) {
        //                final SecurityType wall = config.getType();
        //                final AuthorizationProvider defined = Ut.invokeStatic(providerCls, "provider", config);
        //                if (Objects.nonNull(defined)) {
        //                    handler.addAuthorizationProvider(defined);
        //                }
        //            }
        //
        //            return handler;
        //        } else {
        //            // The class must contain constructor with `(Vertx)`
        //            return ((AuthorizationExtensionHandler) Ut.instance(handlerCls, vertx)).configure(config);
        //        }
        return null;
    }

    protected AuthenticationHandler wrapHandler(final AuthenticationHandler standard, final SecurityMeta aegis) {
        final ChainHandler handler = ChainHandler.all();
        handler.add(standard);
        final AuthenticateBuiltInProvider provider = AuthenticateBuiltInProvider.provider(aegis);
        handler.add(new AuthenticationHandlerImpl(provider) {
            @Override
            public Future<User> authenticate(RoutingContext context) {
                /*
                 * Current handler is not the first handler, the continue validation will process
                 * the user information, the input parameters came from
                 */
                final User user = context.user();
                if (Objects.nonNull(user)) {
                    // R2MO
                    return this.authProvider.authenticate(new TokenCredentials(user.principal()));
                } else {
                    final WebException error = new _401UnauthorizedException("[ ZERO ] 权限不够！");
                    return Future.failedFuture(error);
                }
            }
        });
        return handler;
    }

    protected AuthenticationHandler buildHandler(final AuthenticationProvider standard, final SecurityMeta aegis,
                                                 final HTTPAuthorizationHandler.Type type) {
        final String realm = this.option(aegis, "realm");
        return new HTTPAuthorizationHandler<>(standard, type, realm) {
            @Override
            public Future<User> authenticate(RoutingContext routingContext) {
                return parseAuthorization(routingContext).compose(token -> {
                    if (Objects.isNull(token)) {
                        return Future.failedFuture(new _401UnauthorizedException("[ ZERO ] 权限不够！"));
                    }
                    return this.authProvider.authenticate(new TokenCredentials(token));
                });
            }
        };
    }

    // --------------------------- Sub class only
    protected abstract <T extends AuthenticationProvider> T providerInternal(Vertx vertx, SecurityMeta config);

    protected <T> T option(final SecurityMeta aegis, final String key) {
        final SecurityConfig securityConfig = null; // aegis.item();
        return (T) securityConfig.options().getValue(key, null);
    }

    protected LogOf logger() {
        return LogOf.get(this.getClass());
    }
}
