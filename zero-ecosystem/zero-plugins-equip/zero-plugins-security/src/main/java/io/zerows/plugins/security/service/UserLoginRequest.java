package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserLoginRequest extends LoginRequest {
    public static final TypeLogin TYPE = TypeLogin.PASSWORD;
    private String username;
    private String password;
    private String captcha;
    private String captchaId;

    public void setUsername(final String username) {
        this.username = username;
        this.setId(username);
    }

    public void setPassword(final String password) {
        this.password = password;
        this.setCredential(password);
    }

    @Override
    public TypeLogin type() {
        return TYPE;
    }
}
