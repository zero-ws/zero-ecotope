package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.web.Account;
import io.zerows.sdk.security.WallExecutor;
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
        return userService.loadLogged(request).map(Account::userVx);
    }

    /**
     * 验证步骤一：从 Authorization 请求头提取 credentials 信息，构造对应的 LoginRequest 对象
     *
     * @param credentials 凭证信息
     * @return 登录请求对象
     */
    protected abstract LoginRequest createRequest(JsonObject credentials);
}
