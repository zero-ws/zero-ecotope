package io.zerows.plugins.security.sms;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.security.exception._80241Exception400PasswordRequired;
import io.zerows.plugins.security.service.CaptchaLoginRequest;
import io.zerows.plugins.security.sms.exception._80381Exception400MobileRequired;
import io.zerows.support.Fx;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SmsLoginRequest extends LoginRequest implements CaptchaLoginRequest {
    private String mobile;
    private String captcha;
    private String captchaId;

    public SmsLoginRequest() {
    }

    public SmsLoginRequest(final JsonObject request) {
        this.setMobile(request.getString(LoginID.MOBILE));
        this.setCaptcha(request.getString("captcha"));
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
        this.setId(mobile);
    }

    public void setCaptcha(final String captcha) {
        this.captcha = captcha;
        this.setCredential(captcha);
    }

    @Override
    public TypeLogin type() {
        return TypeLogin.SMS;
    }

    public Future<Void> requestValidated() {
        if (StrUtil.isEmpty(this.mobile)) {
            return Fx.failOut(_80381Exception400MobileRequired.class);
        }
        if (StrUtil.isEmpty(this.captcha)) {
            return Fx.failOut(_80241Exception400PasswordRequired.class, "captcha");
        }
        return Future.succeededFuture();
    }

    @Override
    public String captchaId() {
        return this.captchaId;
    }

    @Override
    public String captcha() {
        return this.captcha;
    }
}
