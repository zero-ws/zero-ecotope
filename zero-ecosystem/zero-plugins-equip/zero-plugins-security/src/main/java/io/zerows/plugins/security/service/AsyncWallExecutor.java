package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Account;
import io.zerows.sdk.security.WallExecutor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 作为 {@link Wall} 的基类，提供异步访问能力，若有必要可以重写相关方法和核心逻辑，主要是验证当前凭证是否生效
 */
@Slf4j
public abstract class AsyncWallExecutor implements WallExecutor {

    @Override
    public Future<User> authenticate(final JsonObject credentials) {
        // 请求访问
        final LoginRequest request = this.createRequest(credentials);
        if (Objects.isNull(request)) {
            // 请求失败
            return Future.succeededFuture();
        }
        // 构造核心组件
        final AsyncUserAt userService = AsyncUserAt.of(request.type());
        if (Objects.isNull(userService)) {
            log.error("[ PLUG ] 未找到用户加载核心组件：SPID = UserAt/{}", request.type());
            return Future.succeededFuture();
        }

        // 加载用户信息，直接做转换 UserAt -> User
        return userService.loadLogged(request)
            .map(Account::userVx)
            // 加载 Profile 信息，默认为 null，由授权组件来处理
            .compose(userService::loadAuthorization);
    }

    /**
     * 验证步骤一：从 Authorization 请求头提取 credentials 信息，构造对应的 LoginRequest 对象
     *
     * @param credentials 凭证信息
     * @return 登录请求对象
     */
    protected abstract LoginRequest createRequest(JsonObject credentials);

    @Override
    public Future<JsonObject> authorize(final User user) {
        final AsyncAuthorization resource = AsyncAuthorization.of();
        Objects.requireNonNull(resource, "[ XMOD ] 授权组件为空 / authorize");
        return resource.seekProfile(user);
    }

    /**
     * <pre>
     *     参数提取核心
     *     - username
     *     - password
     *     - id
     *     - habitus
     *     - session
     *     - metadata
     *       - uri              带路径参数
     *       - requestUri       实际请求参数
     *       - method           请求方法
     *       - view
     *         - view           视图名
     *         - position       视图位置信息
     *     - headers
     *       - X-App-Id
     *       - X-Tenant-Id
     *       - X-Sigma
     * </pre>
     *
     * @param params 资源提取参数
     * @return 资源信息
     */
    @Override
    public Future<JsonObject> resource(final JsonObject params) {
        final JsonObject headers = Ut.valueJObject(params, "headers");
        final String appId = Ut.valueString(headers, KWeb.HEADER.X_APP_ID);
        final AsyncAuthorization resource = AsyncAuthorization.of(appId);
        Objects.requireNonNull(resource, "[ XMOD ] 授权组件为空 / resource");
        return resource.seekResource(params);
    }
}
