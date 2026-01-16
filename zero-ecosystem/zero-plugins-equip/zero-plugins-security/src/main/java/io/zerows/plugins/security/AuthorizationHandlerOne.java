package io.zerows.plugins.security;

import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._403ForbiddenException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.AuthorizationContext;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthorizationHandler;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.metadata.security.SecurityMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * 此处多了一层计算，如果有多层 {@link Wall} 墙的时候，可以根据不同的配置设置不同的处理器，当前版本只有一个
 *
 * @author lang : 2025-10-30
 */
@Slf4j
class AuthorizationHandlerOne implements AuthorizationHandler {
    private static final WebException ERROR_FORBIDDEN = new _403ForbiddenException("身份信息有误，无权访问！");
    private static final WebException ERROR_RESOURCE = new _403ForbiddenException("申请资源无法识别，拒绝访问！");
    private final List<AuthorizationHandlerResource> handlerList = new ArrayList<>();

    private AuthorizationHandlerOne(final List<AuthorizationHandlerResource> handlerList) {
        this.handlerList.addAll(handlerList);
    }

    static AuthorizationHandlerOne create(final Set<SecurityMeta> metaSet) {
        final List<SecurityMeta> metaList = new ArrayList<>(metaSet);
        Collections.sort(metaList);

        final List<AuthorizationHandlerResource> handlerList = new ArrayList<>();
        for (final SecurityMeta meta : metaList) {
            final ProfileResource resource = ProfileResource.buildIn(meta);
            final AuthorizationHandlerResource handler = new AuthorizationHandlerResource(resource);
            handlerList.add(handler);
        }
        return new AuthorizationHandlerOne(handlerList);
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
        for (final AuthorizationHandlerResource handler : this.handlerList) {
            final Future<Boolean> future = handler.handle(context);
            authorized.add(future);
        }
        FnVertx.combineT(authorized).compose(result -> {
            // 任意一个失败就返回异常信息
            for (final Boolean authorizedOk : result) {
                if (!authorizedOk) {
                    return Future.failedFuture(ERROR_RESOURCE);
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

}
