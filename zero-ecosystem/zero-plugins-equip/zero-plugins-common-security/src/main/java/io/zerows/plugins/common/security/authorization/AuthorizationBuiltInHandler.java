package io.zerows.plugins.common.security.authorization;

import io.r2mo.function.Actuator;
import io.r2mo.function.Fn;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._403ForbiddenException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.impl.Http1xServerRequest;
import io.vertx.core.http.impl.Http2ServerRequest;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationContext;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.common.log.Annal;
import io.zerows.epoch.corpus.security.Aegis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Async authorization handler to extract resource from async database
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AuthorizationBuiltInHandler implements AuthorizationHandler {
    private static final Annal LOGGER = Annal.get(AuthorizationBuiltInHandler.class);
    private final transient Collection<AuthorizationProvider> providers;
    private final transient AuthorizationResource resource;
    private BiConsumer<RoutingContext, AuthorizationContext> consumer;

    private AuthorizationBuiltInHandler(final AuthorizationResource resource) {
        this.resource = resource;
        this.providers = new ArrayList<>();
    }

    // Built In
    public static AuthorizationBuiltInHandler create(final Aegis aegis) {
        return new AuthorizationBuiltInHandler(AuthorizationResource.buildIn(aegis));
    }

    // Build by Resource
    public static AuthorizationBuiltInHandler create(final AuthorizationResource resource) {
        return new AuthorizationBuiltInHandler(resource);
    }

    @Override
    public AuthorizationHandler variableConsumer(final BiConsumer<RoutingContext, AuthorizationContext> handler) {
        this.consumer = handler;
        return this;
    }

    @Override
    public void handle(final RoutingContext event) {
        final WebException error = new _403ForbiddenException("[ ZERO ] 资源授权被禁止！");
        if (Objects.isNull(event.user())) {
            /*
             * Whether the user is `null`, if null, return 403 exception
             */
            event.fail(error);
        } else {
            AuthorizationCache.userAuthorized(event, () -> {
                /*
                 * Before starting any potential async operation here
                 * pause parsing the request body, The reason is that we don't want to
                 * lose the body or protocol upgrades for async operations
                 */
                final HttpServerRequest request = event.request();
                this.httpSwitch(request, request::pause);
                // event.request().pause();


                /*
                 * The modification for default to async fetch authorization
                 */
                try {
                    // Context Creation ( Async )
                    // create the authorization context
                    final AuthorizationContext authorizationContext = this.getAuthorizationContext(event);
                    // check or fetch authorizations
                    this.resource.requestResource(event, res -> {
                        if (res.succeeded()) {
                            /*
                             * Iterator and checking
                             */
                            this.checkOrFetchAuthorizations(
                                event,                          // RoutingContext
                                res.result(),                   // Authorization
                                authorizationContext,           // AuthorizationContext
                                this.providers.iterator()       // Iterator<AuthorizationProvider>
                            );
                        } else {
                            // Exception happened
                            final Throwable ex = res.cause();
                            this.httpSwitch(request, request::resume);
                            // event.request().resume();
                            if (Objects.nonNull(ex)) {
                                event.fail(ex);
                            } else {
                                event.fail(error);
                            }
                        }
                    });
                } catch (final RuntimeException ex) {
                    this.httpSwitch(request, request::resume);
                    // event.request().resume();
                    event.fail(ex);
                }
            });
        }
    }

    /*
     * Here are
     */
    private void checkOrFetchAuthorizations(final RoutingContext routingContext,
                                            final Authorization resource,
                                            final AuthorizationContext authorizationContext,
                                            final Iterator<AuthorizationProvider> providers) {
        final HttpServerRequest request = routingContext.request();
        if (resource.match(authorizationContext)) {
            final User user = authorizationContext.user();
            final String session = user.principal().getString(KName.SESSION);
            LOGGER.info("[ Auth ]\u001b[0;32m 403 Authorized successfully \u001b[m for ( {0} ) user: principal = {1}, attribute = {2}",
                session, user.principal(), user.attributes());
            AuthorizationCache.userAuthorize(routingContext, () -> {
                this.httpSwitch(request, request::resume);
                // event.request().resume();
                routingContext.next();
            });
            return;
        }
        if (!providers.hasNext()) {
            // resume as the error handler may allow this request to become valid again
            // Here the provides has no next to process, it means that all the providers
            // fail to 401/403 workflow here.
            this.httpSwitch(request, request::resume);
            // event.request().resume();
            routingContext.fail(new _403ForbiddenException("[ ZERO ] 资源授权被禁止！"));
            return;
        }

        // there was no match, in this case we do the following:
        // 1) contact the next provider we haven't contacted yet
        // 2) if there is a match, get out right away otherwise repeat 1)
        while (providers.hasNext()) {
            final AuthorizationProvider provider = providers.next();
            /*
             * we haven't fetched authorization from this provider yet,
             * in this situation, continue to next provider to validate, after validated successfully
             * you can call next() method of routing context to pass
             */
            //            if (!routingContext.user().authorizations().getProviderIds().contains(provider.getId())) {
            //                final User user = routingContext.user();
            //                provider.getAuthorizations(user, result -> {
            //                    if (result.failed()) {
            //                        LOGGER.warn("[ Auth ] Error occurs when getting authorization - providerId: {0}", provider.getId());
            //                        LOGGER.fatal(result.cause());
            //                    }
            //                    this.checkOrFetchAuthorizations(routingContext, resource, authorizationContext, providers);
            //                });
            //            }
        }
    }

    private AuthorizationContext getAuthorizationContext(final RoutingContext event) {
        final AuthorizationContext result = AuthorizationContext.create(event.user());
        if (this.consumer != null) {
            this.consumer.accept(event, result);
        }
        return result;
    }

    @Override
    public AuthorizationHandler addAuthorizationProvider(final AuthorizationProvider authorizationProvider) {
        Objects.requireNonNull(authorizationProvider);
        this.providers.add(authorizationProvider);
        return this;
    }

    /**
     * {@link Http1xServerRequest}
     * <pre><code>
     *     public HttpServerRequest pause() {
     *         synchronized(this.conn) {
     *             if (this.pending != null) {
     *                 this.pending.pause();
     *             } else {
     *                 this.pending = InboundBuffer.createPaused(this.context, 8L, this.pendingDrainHandler(), this.pendingHandler());
     *             }
     *
     *             return this;
     *         }
     *     }
     * </code></pre>
     * {@link Http2ServerRequest}
     * <pre><code>
     *      public HttpServerRequest pause() {
     *         synchronized((Http2ServerConnection)this.stream.conn) {
     *             // 此处会抛异常：java.lang.IllegalStateException: Request has already been read
     *             this.checkEnded();
     *             this.stream.doPause();
     *             return this;
     *         }
     *     }
     * </code></pre>
     * 当使用 2.0 协议代替 1.0 协议时，关于 {@link HttpServerRequest#pause()} 和 {@link HttpServerRequest#resume()} 调用时会
     * 有所不同，所以此处就要求 2.0 模式下有所变化，只有 1.0 时才调用，如果 2.0 先跳过，防止异常信息
     * <pre><code>
     *     java.lang.IllegalStateException: Request has already been read
     * </code></pre>
     * 异常抛出，若缺失对应内容可以在后期 2.0 熟悉之后来补充，目前从代码上看不出这样的判断是否有影响，停止处理。
     *
     * @param request  HTTP请求
     * @param actuator 执行器
     */
    private void httpSwitch(final HttpServerRequest request, final Actuator actuator) {
        if (HttpVersion.HTTP_2 == request.version()) {
            return;
        }
        Fn.jvmAt(actuator);
    }
}
