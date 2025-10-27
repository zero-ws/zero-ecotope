package io.zerows.extension.commerce.rbac.plugins.authorization;

import io.r2mo.function.Fn;
import io.r2mo.typed.exception.web._403ForbiddenException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.metadata.security.Aegis;
import io.zerows.support.Ut;
import io.zerows.plugins.security.authorization.AuthorizationResource;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ProfileResource implements AuthorizationResource {
    private final transient Aegis aegis;

    private ProfileResource(final Aegis aegis) {
        this.aegis = aegis;
    }

    public static AuthorizationResource create(final Aegis aegis) {
        return new ProfileResource(aegis);
    }

    @Override
    @SuppressWarnings("all")
    public void requestResource(final RoutingContext context, final Handler<AsyncResult<Authorization>> handler) {
        final JsonObject params = AuthorizationResource.parameters(context);
        final Method method = this.aegis.getAuthorizer().getResource();
        Fn.jvmAt(() -> {
            final Future<JsonObject> future = (Future<JsonObject>) method.invoke(this.aegis.getProxy(), params);
            future.onComplete(res -> {
                if (res.succeeded()) {
                    if (Objects.isNull(res.result())) {
                        handler.handle(FnVertx.failOut(_403ForbiddenException.class, "[ R2MO ] 访问被禁止/拒绝！"));
                    } else {
                        final ConcurrentMap<String, Set<String>> profiles = new ConcurrentHashMap<>();
                        Ut.<JsonArray>itJObject(res.result(), (values, field) -> profiles.put(field, Ut.toSet(values)));
                        final Authorization required = ProfileAuthorization.create(profiles);
                        handler.handle(Future.succeededFuture(required));
                    }
                } else {
                    final Throwable ex = res.cause();
                    handler.handle(Future.failedFuture(ex));
                }
            });
        });
    }
}
