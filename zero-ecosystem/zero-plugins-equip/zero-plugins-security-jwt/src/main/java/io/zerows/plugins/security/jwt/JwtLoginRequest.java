package io.zerows.plugins.security.jwt;

import io.r2mo.typed.enums.TypeLogin;
import io.zerows.plugins.security.service.BasicLoginRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JwtLoginRequest extends BasicLoginRequest {

    @Override
    public TypeLogin type() {
        return TypeLogin.JWT;
    }
}
