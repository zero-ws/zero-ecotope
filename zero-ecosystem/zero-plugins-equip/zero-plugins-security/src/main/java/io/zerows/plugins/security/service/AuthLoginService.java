package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserSession;
import io.vertx.core.Future;
import io.zerows.program.Ux;

public class AuthLoginService implements AuthLoginStub {

    @Override
    public Future<UserAt> login(final LoginRequest request) {
        // 提取 UserAt/{TYPE} 专用配置
        final AsyncUserAt userService = AsyncUserAt.of(request.type());
        // 直接执行登录（加载用户信息）
        return userService.loadLogged(request)
            // 切换到 VT 中运行
            .compose(userAt -> Ux.waitVirtual(() -> UserSession.of().userAt(userAt)))
            .compose(Future::succeededFuture);
    }
}
