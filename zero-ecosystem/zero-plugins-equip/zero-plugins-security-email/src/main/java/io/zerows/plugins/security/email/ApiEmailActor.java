package io.zerows.plugins.security.email;

import io.r2mo.base.util.R2MO;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.email.exception._80361Exception400EmailRequired;
import io.zerows.plugins.security.email.exception._80362Exception400EmailFormat;
import io.zerows.plugins.security.email.exception._80363Exception500EmailSending;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.CaptchaStub;
import io.zerows.plugins.security.service.TokenDynamicResponse;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import jakarta.inject.Inject;

import java.util.Objects;

@Queue
public class ApiEmailActor {
    @Inject
    private CaptchaStub captchaStub;

    @Inject
    private AuthLoginStub loginStub;

    @Inject
    private EmailStub emailStub;

    /**
     * 关于发送过程图片验证码的说明
     * <pre>
     *     security:
     *       captcha:
     *         enabled: true      # 图片验证码必须打开（功能要可用），{@link CaptchaStub} 内部会校验
     *       captcha-email:
     *         image: true        # 发送邮件时需要图片验证码，两个条件都满足才会启用图片验证码校验
     * </pre>
     * @param request 基础请求
     * @return 发送结果
     */
    @Address(ApiAddr.API_AUTH_EMAIL_SEND)
    public Future<Boolean> sendAsync(final EmailLoginRequest request) {
        // email 非空 / 格式校验
        final String email = request.getEmail();
        if (Objects.isNull(email)) {
            return Fx.failOut(_80361Exception400EmailRequired.class);
        }
        if (!R2MO.isEmail(email)) {
            return Fx.failOut(_80362Exception400EmailFormat.class, email);
        }
        return this.sendVerify(request)
            // 发送邮件
            .compose(nil -> this.emailStub.sendCaptcha(email))
            // 发送结果
            .compose(sent -> {
                if (!sent) {
                    return Fx.failOut(_80363Exception500EmailSending.class, email);
                }
                return Ux.futureT();
            });
    }

    private Future<Boolean> sendVerify(final EmailLoginRequest request){
        final EmailAuthConfig config = EmailAuthActor.configOf();
        if(config.isImage()){
            // captcha / captchaId 可选非空校验
            return this.captchaStub.validateRequired(request).compose(validated ->
                // 验证码校验
                this.captchaStub.validate(validated.captchaId(), validated.captcha()));
        }
        return Future.succeededFuture(Boolean.TRUE);
    }

    @Address(ApiAddr.API_AUTH_EMAIL_LOGIN)
    public Future<JsonObject> login(final EmailLoginRequest request) {
        return request.requestValidated()
            .compose(verified -> this.loginStub.login(request))
            .compose(userAt -> new TokenDynamicResponse(userAt).response());
    }
}
