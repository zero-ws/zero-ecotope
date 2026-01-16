package io.zerows.plugins.security.ldap;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
public interface ApiLdapAgent {

    @POST
    @Path("/auth/ldap-login")
    @Address(ApiAddr.API_AUTH_LDAP_LOGIN)
    JsonObject login(@BodyParam JsonObject body);
}
