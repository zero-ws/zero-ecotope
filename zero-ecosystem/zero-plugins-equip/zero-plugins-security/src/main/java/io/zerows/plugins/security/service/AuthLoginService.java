package io.zerows.plugins.security.service;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserSession;
import io.vertx.core.Future;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.exception._80242Exception400CaptchaRequired;
import io.zerows.plugins.security.metadata.YmSecurity;
import io.zerows.program.Ux;
import io.zerows.support.Fx;

import java.util.Objects;

public class AuthLoginService implements AuthLoginStub {

    @Override
    public Future<CaptchaRequest> validateCaptcha(final CaptchaRequest request) {
        // 安全配置校验
        final YmSecurity security = SecurityActor.configuration();
        if (Objects.isNull(security)) {
            return Ux.future(request);
        }
        if (!security.isCaptcha()) {
            return Ux.future(request);
        }

        // 启用了图片验证码
        if (StrUtil.isEmpty(request.captchaId())) {
            return Fx.failOut(_80242Exception400CaptchaRequired.class, "captchaId");
        }
        if (StrUtil.isEmpty(request.captcha())) {
            return Fx.failOut(_80242Exception400CaptchaRequired.class, "captcha");
        }
        return Ux.future(request);
    }

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
