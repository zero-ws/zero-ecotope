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

    /**
     * 关于发送短信过程中图片验证码的说明
     * <pre>
     *     security:
     *       captcha:
     *         enabled: true      # 图片验证码必须打开（功能要可用），{@link CaptchaStub} 内部会校验
     *       captcha-sms:
     *         image: true        # 发送短信时需要图片验证码，两个条件都满足才会启用图片验证码校验
     * </pre>
     * @param request 基础请求
     * @return 发送结果
     */
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

        return this.sendVerify(request)
            // 发送短信
            .compose(nil -> this.smsStub.sendCaptcha(mobile))
            // 发送结果
            .compose(sent -> {
                if (!sent) {
                    return Fx.failOut(_80383Exception500MobileSending.class, mobile);
                }
                return Ux.futureT();
            });
    }

    private Future<Boolean> sendVerify(final SmsLoginRequest request){
        final SmsAuthConfig config = SmsAuthActor.configOf();
        if(config.isImage()){
            // captcha / captchaId 可选非空校验
            return this.captchaStub.validateRequired(request).compose(validated ->
                // 验证码校验
                this.captchaStub.validate(validated.captchaId(), validated.captcha())
            );
        }
        return Future.succeededFuture(Boolean.TRUE);
    }

    @Address(ApiAddr.API_AUTH_SMS_LOGIN)
    public Future<JsonObject> login(final SmsLoginRequest request) {
        return request.requestValidated()
            .compose(verified -> this.loginStub.login(request))
            .compose(userAt -> new TokenDynamicResponse(userAt).response());
    }
}
