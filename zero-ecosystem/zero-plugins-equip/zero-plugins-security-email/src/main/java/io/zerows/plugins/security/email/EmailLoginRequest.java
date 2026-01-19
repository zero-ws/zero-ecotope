package io.zerows.plugins.security.email;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.security.email.exception._80361Exception400EmailRequired;
import io.zerows.plugins.security.exception._80241Exception400PasswordRequired;
import io.zerows.plugins.security.service.CaptchaLoginRequest;
import io.zerows.support.Fx;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmailLoginRequest extends LoginRequest implements CaptchaLoginRequest {
    private String email;
    private String captcha;
    private String captchaId;

    public EmailLoginRequest() {
    }

    public EmailLoginRequest(final JsonObject request) {
        this.setEmail(request.getString(LoginID.EMAIL));
        this.setCaptcha(request.getString("captcha"));
    }

    public void setEmail(final String email) {
        this.email = email;
        this.setId(email);
    }

    public void setCaptcha(final String captcha) {
        this.captcha = captcha;
        this.setCredential(captcha);
    }

    @Override
    public TypeLogin type() {
        return TypeLogin.EMAIL;
    }

    public Future<Void> requestValidated() {
        if (StrUtil.isEmpty(this.email)) {
            return Fx.failOut(_80361Exception400EmailRequired.class);
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
