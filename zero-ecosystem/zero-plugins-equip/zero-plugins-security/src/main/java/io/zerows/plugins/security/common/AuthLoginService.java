package io.zerows.plugins.security.common;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.exception._80242Exception400CaptchaRequired;
import io.zerows.plugins.security.metadata.YmSecurity;
import io.zerows.plugins.security.service.UserLoginRequest;
import io.zerows.program.Ux;
import io.zerows.support.Fx;

import java.util.Objects;

public class AuthLoginService implements AuthLoginStub {

    @Override
    public Future<UserLoginRequest> validateCaptcha(final UserLoginRequest request) {
        // 安全配置校验
        final YmSecurity security = SecurityActor.configuration();
        if (Objects.isNull(security)) {
            return Ux.future(request);
        }
        if (!security.isCaptcha()) {
            return Ux.future(request);
        }

        // 启用了图片验证码
        if (StrUtil.isEmpty(request.getCaptchaId())) {
            return Fx.failOut(_80242Exception400CaptchaRequired.class, "captchaId");
        }
        if (StrUtil.isEmpty(request.getCaptcha())) {
            return Fx.failOut(_80242Exception400CaptchaRequired.class, "captcha");
        }
        return Ux.future(request);
    }

    @Override
    public Future<UserAt> login(final UserLoginRequest request) {
        return null;
    }
}
