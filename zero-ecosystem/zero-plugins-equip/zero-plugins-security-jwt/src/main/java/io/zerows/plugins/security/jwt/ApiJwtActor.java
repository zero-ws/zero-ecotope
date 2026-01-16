package io.zerows.plugins.security.jwt;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.CaptchaStub;
import jakarta.inject.Inject;

@Queue
public class ApiJwtActor {
    @Inject
    private CaptchaStub captchaStub;

    @Inject
    private AuthLoginStub loginStub;

    @Address(ApiAddr.API_AUTH_JWT_LOGIN)
    public Future<JsonObject> login(final JwtLoginRequest request) {
        // username, password 非空校验
        return request.requestValidated()
            // captcha / captchaId 可选非空校验
            .compose(this.captchaStub::validateRequired)
            // 验证码校验
            .compose(validated -> this.captchaStub.validate(validated.captchaId(), validated.captcha()))
            // 登录处理
            .compose(nil -> this.loginStub.login(request))
            .compose(userAt -> new JwtLoginResponse(userAt).response());
    }
}
