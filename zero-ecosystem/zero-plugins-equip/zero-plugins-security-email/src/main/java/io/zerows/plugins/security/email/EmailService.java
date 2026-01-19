package io.zerows.plugins.security.email;

import io.r2mo.base.util.R2MO;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.email.EmailClient;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EmailService implements EmailStub {
    private final EmailAuthConfig emailConfig = EmailAuthActor.configOf();
    @Inject
    private EmailClient emailClient;

    @Inject
    private AuthLoginStub loginStub;

    @Override
    public Future<Boolean> sendCaptcha(final String email) {
        // 提取基础参数
        final Duration expiredAt = this.emailConfig.expiredAt();
        final String subject = this.emailConfig.getSubject();
        final String template = this.emailConfig.getTemplate();

        final EmailLoginRequest request = new EmailLoginRequest();
        request.setId(email);
        // 验证码
        return this.loginStub.authorize(request, expiredAt).compose(captcha -> {
            final JsonObject params = new JsonObject();
            params.put("subject", subject);
            params.put("expiredAt", R2MO.uiDate(expiredAt.toSeconds(), TimeUnit.SECONDS));
            params.put("captcha", captcha);
            // 发送短信
            return this.emailClient.sendAsync(template, params, Set.of(email))
                .map(sent -> Ut.valueString(sent, "success"))
                .map(Boolean::parseBoolean);
        });
    }
}
