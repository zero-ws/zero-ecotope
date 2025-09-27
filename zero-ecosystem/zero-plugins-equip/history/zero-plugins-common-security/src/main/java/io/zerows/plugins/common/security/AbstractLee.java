package io.zerows.plugins.common.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import io.vertx.ext.web.handler.impl.HTTPAuthorizationHandler;
import io.zerows.core.constant.em.EmSecure;
import io.zerows.core.exception.WebException;
import io.zerows.core.exception.web._401UnauthorizedException;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.module.security.atom.Aegis;
import io.zerows.module.security.atom.AegisItem;
import io.zerows.module.security.zdk.LeeBuiltIn;
import io.zerows.plugins.common.security.authenticate.AuthenticateBuiltInProvider;
import io.zerows.plugins.common.security.authenticate.ChainHandler;
import io.zerows.plugins.common.security.authorization.AuthorizationBuiltInHandler;
import io.zerows.plugins.common.security.authorization.AuthorizationBuiltInProvider;

import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public abstract class AbstractLee implements LeeBuiltIn {

    // --------------------------- Interface Method

    @Override
    public AuthorizationHandler authorization(final Vertx vertx, final Aegis config) {
        final Class<?> handlerCls = config.getHandler();
        if (Objects.isNull(handlerCls)) {
            // Default profile is no access ( 403 )
            final AuthorizationHandler handler = AuthorizationBuiltInHandler.create(config);
            final AuthorizationProvider provider = AuthorizationBuiltInProvider.provider(config);
            handler.addAuthorizationProvider(provider);

            /*
             * Check whether user defined provider, here are defined provider
             * for current 403 workflow instead of standard workflow here
             */
            final AegisItem item = config.item();
            final Class<?> providerCls = item.getProviderAuthenticate();
            if (Objects.nonNull(providerCls)) {
                final EmSecure.AuthWall wall = config.getType();
                final AuthorizationProvider defined = Ut.invokeStatic(providerCls, "provider", config);
                if (Objects.nonNull(defined)) {
                    handler.addAuthorizationProvider(defined);
                }
            }

            return handler;
        } else {
            // The class must contain constructor with `(Vertx)`
            return ((io.zerows.plugins.common.security.authorization.AuthorizationExtensionHandler) Ut.instance(handlerCls, vertx)).configure(config);
        }
    }

    protected AuthenticationHandler wrapHandler(final AuthenticationHandler standard, final Aegis aegis) {
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
                    final WebException error = new _401UnauthorizedException(getClass());
                    return Future.failedFuture(error);
                }
            }
        });
        return handler;
    }

    protected AuthenticationHandler buildHandler(final AuthenticationProvider standard, final Aegis aegis,
                                                 final HTTPAuthorizationHandler.Type type) {
        final String realm = this.option(aegis, "realm");
        return new HTTPAuthorizationHandler<>(standard, type, realm) {
            @Override
            public Future<User> authenticate(RoutingContext routingContext) {
                return parseAuthorization(routingContext).compose(token -> {
                    if (Objects.isNull(token)) {
                        return Future.failedFuture(new _401UnauthorizedException(getClass()));
                    }
                    return this.authProvider.authenticate(new TokenCredentials(token));
                });
            }
        };
    }

    // --------------------------- Sub class only
    protected abstract <T extends AuthenticationProvider> T providerInternal(Vertx vertx, Aegis config);

    protected <T> T option(final Aegis aegis, final String key) {
        final AegisItem item = aegis.item();
        return (T) item.options().getValue(key, null);
    }

    protected Annal logger() {
        return Annal.get(this.getClass());
    }
}
