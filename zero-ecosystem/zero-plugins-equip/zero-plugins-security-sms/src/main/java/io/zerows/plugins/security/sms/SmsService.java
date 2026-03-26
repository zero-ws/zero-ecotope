package io.zerows.plugins.security.sms;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.session.UserCache;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.sms.SmsClient;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.util.Objects;

public class SmsService implements SmsStub {
    private final SmsAuthConfig smsConfig = SmsAuthActor.configOf();
    @Inject
    private SmsClient smsClient;
    @Inject
    private AuthLoginStub loginStub;

    @Override
    public Future<Boolean> sendCaptcha(final String mobile) {
        // 提取基础参数
        final String template = this.smsConfig.getTemplate();
        final SmsLoginRequest request = new SmsLoginRequest();
        request.setId(mobile);
        // 验证码
        return this.loginStub.authorize(request, this.smsConfig.expiredAt()).compose(captcha -> {
            final JsonObject params = new JsonObject();
            params.put("captcha", captcha);
            // 发送短信
            return this.smsClient.sendAsync(template, params, mobile)
                .map(sent -> Ut.valueString(sent, "success"))
                .map(Boolean::parseBoolean);
        });
    }

    @Override
    public Future<Boolean> verifyRegistration(final SmsLoginRequest request) {
        return this.verifyCaptcha(request);
    }

    private Future<Boolean> verifyCaptcha(final SmsLoginRequest request) {
        final CaptchaArgs captchaArgs = CaptchaArgs.of(request.type(), this.smsConfig.expiredAt());
        return UserCache.of().authorize(request.getId(), captchaArgs).<Future<String>>compose().compose(codeStored -> {
            if (Objects.isNull(codeStored)) {
                return Future.succeededFuture(Boolean.FALSE);
            }
            return Future.succeededFuture(codeStored.equals(request.getCredential()));
        });
    }
}
