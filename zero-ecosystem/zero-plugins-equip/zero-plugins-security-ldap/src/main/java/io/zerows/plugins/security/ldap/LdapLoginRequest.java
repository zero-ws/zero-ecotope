package io.zerows.plugins.security.ldap;

import io.r2mo.typed.enums.TypeLogin;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.zerows.plugins.security.basic.BasicLoginRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LdapLoginRequest extends BasicLoginRequest {
    private String uid;

    @Override
    public TypeLogin type() {
        return TypeLogin.LDAP;
    }

    public UsernamePasswordCredentials credentials() {
        return new UsernamePasswordCredentials(this.getUsername(), this.getPassword());
    }
}
