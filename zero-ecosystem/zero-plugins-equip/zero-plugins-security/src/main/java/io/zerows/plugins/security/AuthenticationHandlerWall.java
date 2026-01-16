package io.zerows.plugins.security;

import io.vertx.core.Completable;
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

    public AuthenticationHandlerWall() {
        super(null);
        this.chainAuthHandlerKey = "__vertx.auth.chain.idx." + HANDLER_KEY_SEQ.getAndIncrement();
    }

    @Override
    public synchronized AuthenticationHandlerWall add(final AuthenticationHandler handler) {
        this.handlers.add((AuthenticationHandlerInternal) handler);
        return this;
    }

    @Override
    public Future<User> authenticate(final RoutingContext context) {
        if (this.handlers.isEmpty()) {
            return Future.succeededFuture();
        }
        final Promise<User> promise = Promise.promise();
        this.iterate(0, context, null, promise);
        return promise.future();
    }

    private void iterate(final int idx, final RoutingContext ctx, final User result, final Completable<User> handler) {
        // 1. 递归终止条件：所有 Handler 都遍历完毕
        if (idx >= this.handlers.size()) {
            // 返回最后一次成功的 User 对象
            handler.complete(result, null);
            return;
        }

        // 2. 获取当前 Handler
        final AuthenticationHandlerInternal authHandler = this.handlers.get(idx);
        authHandler.authenticate(ctx).onComplete(res -> {
            if (res.succeeded()) {
                ctx.put(this.chainAuthHandlerKey, idx);
                // 递归调用 iterate，执行 idx + 1 (下一个 Handler)
                final User verified = this.setAuthorized(ctx, res.result());
                this.iterate(idx + 1, ctx, verified, handler);
            } else {
                ctx.fail(res.cause());
            }
        });
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
        boolean added = false;
        for (final AuthenticationHandlerInternal authHandler : this.handlers) {
            added |= authHandler.setAuthenticateHeader(ctx);
        }
        return added;
    }

    @Override
    public void postAuthentication(final RoutingContext ctx) {
        final Integer idx;
        if ((idx = ctx.get(this.chainAuthHandlerKey)) != null) {
            this.handlers.get(idx).postAuthentication(ctx);
        } else {
            ctx.next();
        }
    }
}
