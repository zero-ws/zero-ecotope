package io.zerows.plugins.security;

import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.UserContext;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerInternal;
import io.vertx.ext.web.impl.UserContextInternal;
import io.zerows.sdk.security.WallHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AuthenticationHandlerWall extends AuthenticationHandlerImpl<AuthenticationProvider> implements WallHandler {
    private static final AtomicInteger HANDLER_KEY_SEQ = new AtomicInteger();
    private final List<AuthenticationHandlerInternal> handlers = new ArrayList<>();
    private final String chainAuthHandlerKey;

    private AuthenticationHandlerInternal finalizer;

    public AuthenticationHandlerWall() {
        super(null);
        this.chainAuthHandlerKey = "__vertx.auth.chain.idx." + HANDLER_KEY_SEQ.getAndIncrement();
    }

    @Override
    public synchronized AuthenticationHandlerWall add(final AuthenticationHandler handler) {
        this.handlers.add((AuthenticationHandlerInternal) handler);
        return this;
    }

    public synchronized void withFinalizer(final AuthenticationHandler finalizer) {
        this.finalizer = (AuthenticationHandlerInternal) finalizer;
    }

    @Override
    public Future<User> authenticate(final RoutingContext context) {
        if (this.handlers.isEmpty()) {
            return Future.succeededFuture();
        }
        final Promise<User> promise = Promise.promise();

        // 1. 执行矩阵编排 (OR 逻辑)
        // 初始调用时，错误为 null
        this.iterate(0, context, promise, null);

        // 2. 连接 Finalizer (AND 逻辑)
        return promise.future().compose(matrixUser -> {
            if (this.finalizer != null) {
                return this.finalizer.authenticate(context);
            }
            return Future.succeededFuture(matrixUser);
        });
    }

    /**
     * 递归迭代器
     *
     * @param idx       当前索引
     * @param ctx       上下文
     * @param promise   结果 Promise
     * @param lastError 上一个 Handler 抛出的异常 (用于追踪链条断裂的真实原因)
     */
    private void iterate(final int idx, final RoutingContext ctx, final Promise<User> promise, final Throwable lastError) {
        // 1. 终止条件：所有 Handler 都遍历完毕
        if (idx >= this.handlers.size()) {
            // 🛑 核心修改：如果有捕获到异常，则抛出最后一次捕获的异常
            // 如果没有任何异常（例如列表为空），则抛出默认 401
            promise.fail(Objects.requireNonNullElseGet(lastError, () -> new _401UnauthorizedException("Authentication failed: No provider accepted the credentials.")));
            return;
        }

        final AuthenticationHandlerInternal authHandler = this.handlers.get(idx);
        try {
            authHandler.authenticate(ctx).onComplete(res -> {
                if (res.succeeded()) {
                    // 矩阵成功：设置 User，完成当前阶段
                    ctx.put(this.chainAuthHandlerKey, idx);
                    final User verified = this.setAuthorized(ctx, res.result());
                    promise.complete(verified);
                } else {
                    // 矩阵失败：尝试下一个，并将当前失败的原因传递下去
                    // 这样当循环结束时，如果全失败了，promise.fail 会拿到最后一个失败原因
                    this.iterate(idx + 1, ctx, promise, res.cause());
                }
            });
        } catch (final Throwable t) {
            // 捕获同步异常，同样传递下去
            this.iterate(idx + 1, ctx, promise, t);
        }
    }

    private User setAuthorized(final RoutingContext ctx, final User user) {
        if (Objects.isNull(user)) {
            return null;
        }
        final UserContext context = ctx.userContext();
        if (context instanceof final UserContextInternal contextInternal) {
            contextInternal.setUser(user);
        }
        return user;
    }

    // ... setAuthenticateHeader 和 postAuthentication 保持不变 ...
    @Override
    public boolean setAuthenticateHeader(final RoutingContext ctx) {
        boolean added = false;
        for (final AuthenticationHandlerInternal authHandler : this.handlers) {
            added |= authHandler.setAuthenticateHeader(ctx);
        }
        return added;
    }

    @Override
    public void postAuthentication(final RoutingContext ctx) {
        final Integer idx = ctx.get(this.chainAuthHandlerKey);
        if (idx != null && idx >= 0 && idx < this.handlers.size()) {
            this.handlers.get(idx).postAuthentication(ctx);
        } else {
            ctx.next();
        }
    }
}