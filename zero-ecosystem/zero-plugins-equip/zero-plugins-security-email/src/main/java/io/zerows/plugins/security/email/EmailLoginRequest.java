package io.zerows.plugins.security.email;

import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
