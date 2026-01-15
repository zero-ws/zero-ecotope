package io.zerows.plugins.security.service;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;
import io.zerows.plugins.security.exception._80240Exception400UsernameRequired;
import io.zerows.plugins.security.exception._80241Exception400PasswordRequired;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BasicLoginRequest extends LoginRequest {
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

    public Future<BasicLoginRequest> requestValidated() {
        if (StrUtil.isEmpty(this.username)) {
            return Fx.failOut(_80240Exception400UsernameRequired.class, LoginID.USERNAME);
        }
        if (StrUtil.isEmpty(this.password)) {
            return Fx.failOut(_80241Exception400PasswordRequired.class, "password");
        }
        return Ux.future(this);
    }
}
