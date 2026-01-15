package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.constant.KName;
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

        // 加载用户信息
        return userService.loadLogged(request).compose(userAt -> {
            // 加载用户基础信息
            if (Objects.isNull(userAt)) {
                return Future.succeededFuture();
            }
            // 构造最终用户信息
            return Future.succeededFuture(this.createUser(userAt));
        });
    }

    /**
     * 验证步骤一：从 Authorization 请求头提取 credentials 信息，构造对应的 LoginRequest 对象
     *
     * @param credentials 凭证信息
     * @return 登录请求对象
     */
    protected abstract LoginRequest createRequest(JsonObject credentials);

    /**
     * 认证基础
     *
     * @param userAt 核心用户信息
     * @return 认证用户
     */
    protected User createUser(final UserAt userAt) {
        final MSUser user = userAt.logged();
        if (Objects.isNull(user)) {
            return null;
        }
        /*
         * 构造身份主体 Principal 信息，此处手动组装 JsonObject，防止 password cannot be null 的错误
         *
         */
        final JsonObject principal = new JsonObject();
        principal.put(KName.USERNAME, user.getUsername());
        principal.put(KName.PASSWORD, user.getPassword());
        principal.put(KName.ID, user.getId().toString());
        // 鉴于旧版标识基本信息，此处还需要执行 habitus 对应的数据计算，此处 habitus 是后续执行过程中的核心
        principal.put(KName.HABITUS, user.getId().toString());
        final User authUser = User.create(principal, userAt.data().data());
        /*
         * 后续处理，加载用户信息
         */
        return authUser;
    }
}
