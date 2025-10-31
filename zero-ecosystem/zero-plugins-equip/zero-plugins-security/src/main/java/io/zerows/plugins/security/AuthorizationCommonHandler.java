package io.zerows.plugins.security;

import com.hazelcast.internal.json.JsonObject;
import io.r2mo.function.Fn;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._403ForbiddenException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.impl.Http1xServerRequest;
import io.vertx.core.http.impl.Http2ServerRequest;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationContext;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.auth.authorization.Authorizations;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.metadata.security.SecurityMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 此处多了一层计算，如果有多层 {@link Wall} 墙的时候，可以根据不同的配置设置不同的处理器，当前版本只有一个
 *
 * @author lang : 2025-10-30
 */
@Slf4j
class AuthorizationCommonHandler implements AuthorizationHandler {
    private final List<ResourceHandler> handlerList = new ArrayList<>();
    private static final WebException ERROR_FORBIDDEN = new _403ForbiddenException("[ ZERO ] 无权限访问申请资源！");

    private AuthorizationCommonHandler(final List<ResourceHandler> handlerList) {
        this.handlerList.addAll(handlerList);
    }

    static AuthorizationCommonHandler create(final Set<SecurityMeta> metaSet) {
        final List<SecurityMeta> metaList = new ArrayList<>(metaSet);
        Collections.sort(metaList);

        final List<ResourceHandler> handlerList = new ArrayList<>();
        for (final SecurityMeta meta : metaList) {
            final ProfileResource resource = ProfileResource.buildIn(meta);
            final ResourceHandler handler = new ResourceHandler(resource);
            handlerList.add(handler);
        }
        return new AuthorizationCommonHandler(handlerList);
    }

    @Override
    public AuthorizationHandler addAuthorizationProvider(final AuthorizationProvider authorizationProvider) {
        this.handlerList.forEach(handler -> handler.addAuthorizationProvider(authorizationProvider));
        return this;
    }

    @Override
    public AuthorizationHandler variableConsumer(final BiConsumer<RoutingContext, AuthorizationContext> variableFn) {
        this.handlerList.forEach(handler -> handler.variableConsumer(variableFn));
        return this;
    }

    @Override
    public void handle(final RoutingContext context) {
        final User user = context.user();
        // 小概率判断：用户对象不存在，直接 403，正常来说应该之前 401 已经判断过了
        if (Objects.isNull(user)) {
            // 直接异常出局
            context.fail(ERROR_FORBIDDEN);
        }


        // 依次执行单个授权处理器
        final List<Future<Boolean>> authorized = new ArrayList<>();
        for (final ResourceHandler handler : this.handlerList) {
            final Future<Boolean> future = handler.handle(context);
            authorized.add(future);
        }
        FnVertx.combineT(authorized).compose(result -> {
            // 任意一个失败就返回异常信息
            for (final Boolean authorizedOk : result) {
                if (!authorizedOk) {
                    return Future.failedFuture(ERROR_FORBIDDEN);
                }
            }
            return Future.succeededFuture(Boolean.TRUE);
        }).onComplete(done -> {
            if (done.succeeded()) {
                final boolean authorizedOk = done.result();
                if (!authorizedOk) {
                    // 理论上不会走到这里来
                    context.fail(ERROR_FORBIDDEN);
                    return;
                }
                // 全部通过，继续下一个处理器
                context.next();
            } else {
                context.fail(done.cause());
            }
        });
    }

    /**
     * 单资源授权处理器
     *
     * @author lang : 2025-10-31
     */
    @Slf4j
    private static class ResourceHandler {

        private final ProfileResource resource;
        private final Collection<AuthorizationProvider> providers = new ArrayList<>();
        private BiConsumer<RoutingContext, AuthorizationContext> variableFn;

        ResourceHandler(final ProfileResource resource) {
            this.resource = resource;
        }


        public void addAuthorizationProvider(
            final AuthorizationProvider authorizationProvider) {
            this.providers.add(authorizationProvider);
        }

        public void variableConsumer(
            final BiConsumer<RoutingContext, AuthorizationContext> variableFn) {
            this.variableFn = variableFn;
        }

        public Future<Boolean> handle(final RoutingContext context) {
            final User user = context.user();
            // 小概率判断：用户对象不存在，直接 403，正常来说应该之前 401 已经判断过了
            if (Objects.isNull(user)) {
                return Future.succeededFuture(Boolean.FALSE);
            }

            final HttpServerRequest request = context.request();
            final Future<Boolean> waitFuture = this.waitCached(context);
            return waitFuture.compose(waitFor -> {
                if (!waitFor) {
                    // 缓存命中，跳过授权，直接通过
                    return Future.succeededFuture(Boolean.TRUE);
                }


                /*
                 * 在执行任意潜在的异步操作之前，先 pause 请求体数据，原因是不想在异步操作过程中丢失请求体数据或者协议升级
                 */
                this.requestPause(request);

                try {
                    /*
                     * 执行授权处理器
                     */
                    return this.waitAuthorized(context, request);
                } catch (final Throwable ex) {
                    this.requestResume(request);
                    return Future.failedFuture(ex);
                }
            }).otherwise(error -> {
                // 执行过程异常！
                this.requestResume(request);
                context.fail(error);
                return null;
            });
        }

        private Future<Boolean> waitAuthorized(final RoutingContext context, final HttpServerRequest request) {
            final AuthorizationContext authContext = this.requestContext(context);
            return this.resource.requestResource(context).compose(resourceAuthorization -> {
                // 直接执行授权提供者链的首次验证
                if (resourceAuthorization.match(authContext)) {
                    // 匹配合法，返回 True，将会执行下一个 Handler
                    final User user = authContext.user();
                    final String session = user.principal().getString(KName.SESSION);
                    log.info("[ ZERO ] ( Secure ) 403 用户授权成功：session = {}, 用户User: principal = {} / attributes = {}",
                        session, user.principal(), user.attributes());
                    // 缓存写入后返回
                    return this.waitCached(context, user);
                }


                // 不匹配的时候，本来应该返回 403
                if (this.providers.isEmpty()) {
                    /*
                     * 这种模式下没有后续的提供者执行，所以可以直接返回 403
                     */
                    this.requestResume(request);
                    // 资源请求失败，验证不成功
                    return Future.succeededFuture(Boolean.FALSE);
                }


                /*
                 * 此处不匹配，持续做如下处理，从 Provider 中找到合法的 Provider 再校验，这种情况下不再从 provider 中去提取
                 * Authorization，而是直接使用 resourceAuthorization 进行匹配，只要有一个成功就可以返回 True，否则返回
                 * False
                 */
                final Iterator<AuthorizationProvider> iterator = this.providers.iterator();
                final User user = context.user();
                final Authorizations authorizations = user.authorizations();
                // 默认是不匹配
                Future<Boolean> waitUpdate = Future.succeededFuture(Boolean.FALSE);
                while (iterator.hasNext()) {
                    final AuthorizationProvider provider = iterator.next();
                    if (!authorizations.contains(provider.getId())) {
                        // 不包含时直接转下一个迭代
                        continue;
                    }


                    // 如果包含则要授权
                    waitUpdate = waitUpdate
                        // 1. 先更新用户信息
                        .compose(nil -> provider.getAuthorizations(user))
                        // 2. 再做一次匹配校验
                        .compose(updated -> this.waitCached(context, user));
                }
                return waitUpdate;
            });
        }

        private Future<Boolean> waitCached(final RoutingContext context, final User user) {
            final Rapid<String, JsonObject> cached = Rapid.user(user);
            return cached.read(KWeb.CACHE.User.AUTHORIZATION).compose(authorized -> {
                final String requestId = this.requestResourceId(context);
                final JsonObject waitFor = Objects.isNull(authorized) ? new JsonObject() : authorized;
                waitFor.add(requestId, Boolean.TRUE);
                return cached.write(KWeb.CACHE.User.AUTHORIZATION, waitFor).compose(nil -> {
                    // 恢复请求处理
                    this.requestResume(context.request());
                    return Future.succeededFuture(Boolean.TRUE);
                });
            }).otherwise(error -> {
                // 执行过程异常！
                context.fail(error);
                return null;
            });
        }

        private Future<Boolean> waitCached(final RoutingContext context) {
            final User user = context.user();
            final Rapid<String, JsonObject> cached = Rapid.user(user);
            return cached.read(KWeb.CACHE.User.AUTHORIZATION).compose(res -> {
                // 初始化授权结果
                final JsonObject initialized = Objects.isNull(res) ? new JsonObject() : res;
                // 提取当前资源的资源标识
                final String resource = this.requestResourceId(context);
                final boolean authorized = initialized.getBoolean(resource, Boolean.FALSE);
                if (authorized) {
                    // 跳过认证
                    final String session = user.principal().getString(KName.HABITUS);
                    log.info("[ ZERO ] ( Secure ) 403 用户授权命中缓存：session = {} / resource = {}",
                        session, resource);
                    return Future.succeededFuture(Boolean.FALSE);
                } else {
                    // 等待认证
                    return Future.succeededFuture(Boolean.TRUE);
                }
            }).otherwise(error -> {
                // 执行过程异常！
                context.fail(error);
                return null;
            });
        }

        private String requestResourceId(final RoutingContext context) {
            final HttpServerRequest request = context.request();
            return request.method() + " " + request.path();
        }

        // ---------------- 针对 HTTP 2 的特殊处理

        /**
         * 特殊解决方案，针对 HTTP/2 和 HTTP/1.1 的差异化处理部分
         * {@link Http1xServerRequest}
         * <pre>
         *     public HttpServerRequest pause() {
         *         synchronized(this.conn) {
         *             if (this.pending != null) {
         *                 this.pending.pause();
         *             } else {
         *                 this.pending = InboundBuffer.createPaused(this.configure, 8L, this.pendingDrainHandler(), this.pendingHandler());
         *             }
         *             return this;
         *         }
         *     }
         * </pre>
         * {@link Http2ServerRequest}
         * <pre>
         *      public HttpServerRequest pause() {
         *         synchronized((Http2ServerConnection)this.stream.conn) {
         *             // 此处会抛异常：java.lang.IllegalStateException: Request has already been read
         *             this.checkEnded();
         *             this.stream.doPause();
         *             return this;
         *         }
         *     }
         * </pre>
         * 当使用 2.0 协议代替 1.0 协议时，关于 {@link HttpServerRequest#pause()} 和 {@link HttpServerRequest#resume()} 调用时会
         * 有所不同，所以此处就要求 2.0 模式下有所变化，只有 1.0 时才调用，如果 2.0 先跳过，防止异常信息
         * <pre><code>
         *     java.lang.IllegalStateException: Request has already been read
         * </code></pre>
         * 异常抛出，若缺失对应内容可以在后期 2.0 熟悉之后来补充，目前从代码上看不出这样的判断是否有影响，停止处理。
         *
         * @param request HTTP 请求对象
         */
        private void requestPause(final HttpServerRequest request) {
            if (HttpVersion.HTTP_2 == request.version()) {
                return;
            }
            Fn.jvmAt(request::pause);
        }

        private void requestResume(final HttpServerRequest request) {
            if (HttpVersion.HTTP_2 == request.version()) {
                return;
            }
            Fn.jvmAt(request::resume);
        }


        /**
         * 构造授权上下文，此处的 {@link User} 一定不会为 null，不仅如此，此处还可以绑定函数实现对授权上下文进行二次加工
         *
         * @param context 路由上下文
         *
         * @return 授权上下文
         */
        private AuthorizationContext requestContext(final RoutingContext context) {
            final User user = context.user();
            /*
             * 更改默认的异步授权获取流程
             */
            final AuthorizationContext contextAuthorized = AuthorizationContext.create(user);
            if (Objects.nonNull(this.variableFn)) {
                this.variableFn.accept(context, contextAuthorized);
            }
            return contextAuthorized;
        }
    }
}
