package io.zerows.plugins.security.email;

import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.security.email.exception._80351Exception400EmailRequired;
import io.zerows.plugins.security.exception._80241Exception400PasswordRequired;
import io.zerows.support.Fx;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmailLoginRequest extends LoginRequest {
    private String email;
    private String captcha;

    public EmailLoginRequest() {
    }

    public EmailLoginRequest(final JsonObject request) {
        this.setEmail(request.getString(LoginID.EMAIL));
        this.setCaptcha(request.getString("captcha"));
        // 如果使用 JObject 构造，构造完成后验证！
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
        if (Objects.isNull(this.email)) {
            return Fx.failOut(_80351Exception400EmailRequired.class);
        }
        if (Objects.isNull(this.captcha)) {
            return Fx.failOut(_80241Exception400PasswordRequired.class, "captcha");
        }
        return Future.succeededFuture();
    }
}
