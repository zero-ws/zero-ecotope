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

/**
 * 🟢 认证墙 (Fail-Over 模式)
 * 核心逻辑：Handler 之间是 OR 关系。只要有一个 Handler 认证成功，即视为通过。
 * 解决痛点：防止 Basic Handler 因为看不懂 Bearer Token 而直接中断请求，导致 JWT Handler 无法执行。
 */
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
            // 没有配置任何处理器，直接通过（或者根据安全策略决定是否拒绝）
            return Future.succeededFuture();
        }
        final Promise<User> promise = Promise.promise();

        // 1. 启动矩阵编排 (OR 逻辑迭代)
        this.iterate(0, context, promise, null);

        // 2. 连接 Finalizer (AND 逻辑，严出)
        return promise.future().compose(matrixUser -> {
            if (this.finalizer != null) {
                // Finalizer 通常用于将 User 转换为业务 Account，或者做最后的统一校验
                return this.finalizer.authenticate(context);
            }
            return Future.succeededFuture(matrixUser);
        });
    }

    /**
     * 🟢 核心递归逻辑：Fail-Over 机制
     * * @param idx       当前尝试的 Handler 索引
     *
     * @param ctx       上下文
     * @param promise   整体结果 Promise
     * @param lastError 上一个 Handler 失败的原因 (仅用于所有都失败时抛出)
     */
    private void iterate(final int idx, final RoutingContext ctx, final Promise<User> promise, final Throwable lastError) {
        // [终止条件]：所有 Handler 都尝试完毕，依然没有成功
        if (idx >= this.handlers.size()) {
            // 🛑 核心修复点 🛑
            // 不要直接把 lastError 抛给前端！
            // 因为 lastError 往往是链条中最后一个 Handler（通常是 JWT）报出的格式错误，
            // 它会覆盖掉前面 Handler (如 Basic) 真正有价值的错误（如密码错误）。

            // 1. 记录日志供服务端排查 (可选)
            if (lastError != null) {
                log.debug("[ PLUG ] (Security) All auth handlers failed. Last error was: {}", lastError.getMessage());
            }

            // 2. 对前端返回统一的、通用的 401 错误
            // 这样无论用户是用 Basic 还是 JWT，错了就是 "Authentication failed"，不会有歧义
            promise.fail(new _401UnauthorizedException("Authentication failed: Invalid credentials."));
            return;
        }

        final AuthenticationHandlerInternal authHandler = this.handlers.get(idx);

        // 🌟 关键点：使用 try-catch 包裹同步异常，使用 onComplete 处理异步结果
        try {
            authHandler.authenticate(ctx).onComplete(res -> {
                if (res.succeeded()) {
                    // ✅ 成功：任意一个 Handler 成功，即视为整体成功！
                    // 记录是哪个 Handler 成功的 (用于 postAuthentication)
                    ctx.put(this.chainAuthHandlerKey, idx);

                    final User verified = this.setAuthorized(ctx, res.result());
                    // 立即完成，不再尝试后续 Handler
                    promise.complete(verified);
                } else {
                    // ❌ 失败：当前 Handler 不认这个 Token (例如 Basic Handler 看到 Bearer Token)
                    // 吞掉异常，继续尝试下一个 (idx + 1)
                    if (log.isDebugEnabled()) {
                        log.debug("[ PLUG ] ( Security ) Handler [{}] skipped due to error: {}",
                            authHandler.getClass().getSimpleName(), res.cause().getMessage());
                    }
                    this.iterate(idx + 1, ctx, promise, res.cause());
                }
            });
        } catch (final Throwable t) {
            // ❌ 同步异常：也吞掉，继续尝试下一个
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

    @Override
    public boolean setAuthenticateHeader(final RoutingContext ctx) {
        // 聚合所有 Handler 的 WWW-Authenticate 头
        boolean added = false;
        for (final AuthenticationHandlerInternal authHandler : this.handlers) {
            added |= authHandler.setAuthenticateHeader(ctx);
        }
        return added;
    }

    @Override
    public void postAuthentication(final RoutingContext ctx) {
        // 🌟 关键点：谁认证成功的，就由谁来处理后置逻辑
        final Integer idx = ctx.get(this.chainAuthHandlerKey);
        if (idx != null && idx >= 0 && idx < this.handlers.size()) {
            this.handlers.get(idx).postAuthentication(ctx);
        } else {
            // 如果没有记录索引（可能是 session 恢复的 user），直接 next
            ctx.next();
        }
    }
}