package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.typed.common.Kv;
import io.vertx.core.Future;
import io.zerows.plugins.security.exception._80245Exception404AuthService;
import io.zerows.program.Ux;
import io.zerows.support.Fx;

import java.time.Duration;
import java.util.Objects;

public class AuthLoginService implements AuthLoginStub {
    @Override
    public Future<String> authorize(final LoginRequest request, final Duration expiredAt) {
        // 提取 PreAuth/{TYPE} 专用配置
        final AsyncPreAuth authService = AsyncPreAuth.of(request.type());
        if (Objects.isNull(authService)) {
            return Fx.failOut(_80245Exception404AuthService.class, "PreAuth/" + request.type().name());
        }
        // 直接执行生成
        return authService.authorize(request.getId()).compose(generated -> {
            final UserCache cache = UserCache.of();
            final CaptchaArgs captchaArgs = CaptchaArgs.of(request.type(), expiredAt);
            return Ux.waitVirtual(() -> {
                cache.authorize(generated, captchaArgs);
                return generated;
            });
        }).map(Kv::value);
    }

    @Override
    public Future<UserAt> login(final LoginRequest request) {
        // 提取 UserAt/{TYPE} 专用配置
        final AsyncUserAt userService = AsyncUserAt.of(request.type());
        if (Objects.isNull(userService)) {
            return Fx.failOut(_80245Exception404AuthService.class, "UserAt/" + request.type().name());
        }
        // 直接执行登录（加载用户信息）
        return userService.loadLogged(request)
            // 还有一步，权限 401 的基本表处理
            .compose(Future::succeededFuture);
    }
}
