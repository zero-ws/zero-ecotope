package io.zerows.extension.commerce.rbac.plugins.authorization;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.metadata.security.SecurityMeta;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ProfileResource implements io.zerows.plugins.security.ProfileResource {
    private final transient SecurityMeta aegis;

    private ProfileResource(final SecurityMeta aegis) {
        this.aegis = aegis;
    }

    public static io.zerows.plugins.security.ProfileResource create(final SecurityMeta aegis) {
        return new ProfileResource(aegis);
    }

    @Override
    @SuppressWarnings("all")
    public void requestResource(final RoutingContext context, final Handler<AsyncResult<Authorization>> handler) {
        //        final JsonObject params = AuthorizationResource.parameters(context);
        //        final Method method = this.aegis.getAuthorizer().getResource();
        //        Fn.jvmAt(() -> {
        //            final Future<JsonObject> future = (Future<JsonObject>) method.invoke(this.aegis.getProxy(), params);
        //            future.onComplete(res -> {
        //                if (res.succeeded()) {
        //                    if (Objects.isNull(res.result())) {
        //                        handler.handle(FnVertx.failOut(_403ForbiddenException.class, "[ R2MO ] 访问被禁止/拒绝！"));
        //                    } else {
        //                        final ConcurrentMap<String, Set<String>> profiles = new ConcurrentHashMap<>();
        //                        Ut.<JsonArray>itJObject(res.result(), (values, field) -> profiles.put(field, Ut.toSet(values)));
        //                        final Authorization required = ProfileAuthorization.create(profiles);
        //                        handler.handle(Future.succeededFuture(required));
        //                    }
        //                } else {
        //                    final Throwable ex = res.cause();
        //                    handler.handle(Future.failedFuture(ex));
        //                }
        //            });
        //        });
    }
}
