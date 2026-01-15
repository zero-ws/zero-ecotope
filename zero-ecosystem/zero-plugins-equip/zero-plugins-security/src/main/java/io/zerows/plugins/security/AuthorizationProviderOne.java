package io.zerows.plugins.security;

import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._403ForbiddenException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.sdk.security.WallExecutor;

import java.util.Objects;

/**
 * @author lang : 2025-10-29
 */
class AuthorizationProviderOne implements AuthorizationProvider {

    private final SecurityMeta meta;

    public AuthorizationProviderOne(final SecurityMeta meta) {
        this.meta = meta;
    }

    @Override
    public String getId() {
        // 和 SecurityType 执行绑定，得到对应信息
        return this.meta.getType().key();
    }

    /**
     * 授权专用方法，针对已经登录的用户进行授权，直接调用 {@link WallExecutor#authorize(User)} 方法，返回的 Json 结构如下
     * <pre>
     *     {
     *         "field-01": ["values-011", "values-012", "..."],
     *         "field-02": ["values-021", "..."]
     *     }
     * </pre>
     * 此处已经是认证之后的，简单说就是 {@link User} 是验证过的，然后通过 {@link WallExecutor} 来进行授权信息的提取，所以
     * 此处的 {@link User} 一定不会为空！
     *
     * @param user 查询用户
     * @return 授权结果
     */
    @Override
    public Future<Void> getAuthorizations(final User user) {
        Objects.requireNonNull(user, "[ PLUG ] 授权用户信息不能为空！");
        final WallExecutor executor = this.meta.getProxy();
        if (Objects.isNull(executor)) {
            return Future.failedFuture(new _403ForbiddenException("[ PLUG ] 授权执行器未配置！"));
        }

        final Future<JsonObject> authorized = executor.authorize(user);
        final Promise<Void> promise = Promise.promise();
        authorized.onComplete(validated -> {
            if (validated.succeeded()) {
                // 实际权限
                final Authorization userAuthorization = ProfileAuthorization.create(validated.result());
                user.authorizations().put(this.getId(), userAuthorization);
                promise.complete();
            } else {
                final WebException failure = new _403ForbiddenException(validated.cause().getMessage());
                promise.fail(failure);
            }
        });
        return promise.future();
    }
}
