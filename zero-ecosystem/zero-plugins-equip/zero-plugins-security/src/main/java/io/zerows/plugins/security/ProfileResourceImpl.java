package io.zerows.plugins.security;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * There are some difference between vertx and zero, in vertx, the resource and permissions are defined in
 * Static Mode, it means that you won't fetch resource in your @AuthorizedResource method, in this kind of
 * situation, you can pass `Authorization` object and keep matching unique one. But in zero framework, the
 * `Authorization` object is calculated by resource in each request.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class ProfileResourceImpl implements ProfileResource {
    private final transient SecurityMeta meta;

    ProfileResourceImpl(final SecurityMeta meta) {
        this.meta = meta;
    }

    @Override
    @SuppressWarnings("all")
    public void requestResource(final RoutingContext context, final Handler<AsyncResult<Authorization>> handler) {
        final JsonObject params = ProfileParameter.build(context);
        final WallExecutor executor = this.meta.getProxy();
        final Future<JsonObject> resource = executor.resource(params);
        resource.onSuccess(result -> {
            /*
             * 特殊处理，根据权限本身进行计算，得到最终的 Authorization 对象以及相关结果，通过它来做相关拦截
             * 暂时版本不考虑 Or / And 的复杂权限计算，仅仅支持单一权限点的计算
             */
            final Authorization required = ProfileAuthorization.create(Set.of());
            handler.handle(Future.succeededFuture(required));
        }).onFailure(error -> {
            log.error(error.getMessage(), error);
            handler.handle(Future.failedFuture(error));
        });
    }
}
