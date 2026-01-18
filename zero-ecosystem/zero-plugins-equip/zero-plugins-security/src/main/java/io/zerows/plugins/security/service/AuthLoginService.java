package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;
import io.zerows.plugins.security.exception._80245Exception404AuthService;
import io.zerows.support.Fx;

import java.util.Objects;

public class AuthLoginService implements AuthLoginStub {

    @Override
    public Future<UserAt> login(final LoginRequest request) {
        // 提取 UserAt/{TYPE} 专用配置
        final AsyncUserAt userService = AsyncUserAt.of(request.type());
        if (Objects.isNull(userService)) {
            return Fx.failOut(_80245Exception404AuthService.class, request.type());
        }
        // 直接执行登录（加载用户信息）
        return userService.loadLogged(request)
            // 还有一步，权限 401 的基本表处理
            .compose(Future::succeededFuture);
    }
}
