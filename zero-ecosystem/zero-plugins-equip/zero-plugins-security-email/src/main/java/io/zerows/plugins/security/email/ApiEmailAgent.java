package io.zerows.plugins.security.email;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
public interface ApiEmailAgent {

    @POST
    @Path("/auth/email-send")
    @Address(ApiAddr.API_AUTH_EMAIL_SEND)
    JsonObject sendAsync(@BodyParam JsonObject params);

    @Path("/auth/email-login")
    @POST
    @Address(ApiAddr.API_AUTH_EMAIL_LOGIN)
    JsonObject login(@BodyParam JsonObject body);
}
