package io.zerows.plugins.security.sms;

import io.r2mo.base.util.R2MO;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.CaptchaStub;
import io.zerows.plugins.security.service.TokenDynamicResponse;
import io.zerows.plugins.security.sms.exception._80381Exception400MobileRequired;
import io.zerows.plugins.security.sms.exception._80382Exception400MobileFormat;
import io.zerows.plugins.security.sms.exception._80383Exception500MobileSending;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import jakarta.inject.Inject;

import java.util.Objects;

@Queue
public class ApiSmsActor {

    @Inject
    private CaptchaStub captchaStub;

    @Inject
    private AuthLoginStub loginStub;

    @Inject
    private SmsStub smsStub;

    @Address(ApiAddr.API_AUTH_SMS_SEND)
    public Future<Boolean> sendAsync(final SmsLoginRequest request) {
        // mobile 非空 / 格式校验
        final String mobile = request.getMobile();
        if (Objects.isNull(mobile)) {
            return Fx.failOut(_80381Exception400MobileRequired.class);
        }
        if (!R2MO.isMobile(mobile)) {
            return Fx.failOut(_80382Exception400MobileFormat.class, mobile);
        }
        // captcha / captchaId 可选非空校验
        return this.captchaStub.validateRequired(request)
            // 验证码校验
            .compose(validated -> this.captchaStub.validate(validated.captchaId(), validated.captcha()))
            // 发送邮件
            .compose(nil -> this.smsStub.sendCaptcha(mobile))
            // 发送结果
            .compose(sent -> {
                if (!sent) {
                    return Fx.failOut(_80383Exception500MobileSending.class, mobile);
                }
                return Ux.futureT();
            });
    }

    @Address(ApiAddr.API_AUTH_SMS_LOGIN)
    public Future<JsonObject> login(final SmsLoginRequest request) {
        return request.requestValidated()
            .compose(verified -> this.loginStub.login(request))
            .compose(userAt -> new TokenDynamicResponse(userAt).response());
    }
}
