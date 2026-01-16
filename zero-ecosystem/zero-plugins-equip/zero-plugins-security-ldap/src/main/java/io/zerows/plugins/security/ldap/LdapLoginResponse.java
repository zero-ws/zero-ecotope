package io.zerows.plugins.security.ldap;

import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.platform.enums.SecurityType;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.service.AsyncLoginResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LdapLoginResponse extends AsyncLoginResponse {
    private final SecurityConfig securityConfig;

    public LdapLoginResponse(final UserAt userAt) {
        super(userAt);
        this.securityConfig = SecurityActor.configOf(SecurityType.LDAP);
    }

    @Override
    public Future<JsonObject> response() {
        final JsonObject result = new JsonObject();
        return Future.succeededFuture(new JsonObject());
    }

    @Override
    protected String getToken(final UserAt user) {
        return "";
    }
}
