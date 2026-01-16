package io.zerows.plugins.security;

import io.r2mo.typed.exception.web._403ForbiddenException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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
    public Future<Authorization> requestResource(final RoutingContext context) {
        final JsonObject params = ProfileParameter.build(context);
        final WallExecutor executor = this.meta.getProxy();
        final Future<JsonObject> resource = executor.resource(params);
        return resource.compose(result -> {
            if (Objects.isNull(result)) {
                return Future.failedFuture(new _403ForbiddenException("访问被禁止/拒绝！"));
            }
            // 需求权限
            final Authorization required = ProfileAuthorization.create(result);
            return Future.succeededFuture(required);
        });
    }
}
