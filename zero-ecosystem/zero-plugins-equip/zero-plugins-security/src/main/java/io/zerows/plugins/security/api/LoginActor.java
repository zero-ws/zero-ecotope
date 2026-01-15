package io.zerows.plugins.security.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.SecurityUser;
import io.zerows.plugins.security.basic.BasicLoginRequest;
import io.zerows.plugins.security.common.AuthLoginStub;
import io.zerows.plugins.security.common.CaptchaStub;
import io.zerows.plugins.security.exception._80216Exception403CaptchaProfile;
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

        return request.requestValidated()// username, password 非空校验
            .compose(this.loginStub::validateCaptcha) // captcha / captchaId 可选非空校验
            .compose(validated -> this.captchaStub.validate(validated.getCaptchaId(), validated.getCaptcha()))
            .compose(nil -> this.loginStub.login(request))
            .compose(userAt -> {
                System.out.println(userAt.id());
                return null;
            });
    }

    @Address(Addr.API_AUTH_CAPTCHA)
    public Future<JsonObject> captcha(final User user) {
        if (Objects.nonNull(user)) {
            // 80216 已登录用户不允许获取验证码
            final String id = SecurityUser.id();
            return Fx.failOut(_80216Exception403CaptchaProfile.class, id);
        }
        return this.captchaStub.generate();
    }
}
