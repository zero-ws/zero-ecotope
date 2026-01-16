package io.zerows.plugins.security.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.basic.BasicLoginRequest;
import io.zerows.plugins.security.basic.BasicLoginResponse;
import io.zerows.plugins.security.exception._80216Exception403CaptchaProfile;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.CaptchaStub;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import jakarta.inject.Inject;

import java.util.Objects;

@Queue
public class LoginActor {

    @Inject
    private CaptchaStub captchaStub;

    @Inject
    private AuthLoginStub loginStub;

    @Address(Addr.API_AUTH_LOGIN)
    public Future<JsonObject> login(final BasicLoginRequest request) {
        // username, password 非空校验
        return request.requestValidated()
            // captcha / captchaId 可选非空校验
            .compose(this.captchaStub::validateRequired)
            // 验证码校验
            .compose(validated -> this.captchaStub.validate(validated.captchaId(), validated.captcha()))
            // 登录处理
            .compose(nil -> this.loginStub.login(request))
            .compose(userAt -> new BasicLoginResponse(userAt).response());
    }

    @Address(Addr.API_AUTH_CAPTCHA)
    public Future<JsonObject> captcha(final User user) {
        if (Objects.nonNull(user)) {
            // 80216 已登录用户不允许获取验证码
            final String id = Ux.userId();
            return Fx.failOut(_80216Exception403CaptchaProfile.class, id);
        }
        return this.captchaStub.generate();
    }
}
