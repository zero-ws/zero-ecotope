package io.zerows.plugins.security.sms;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
public interface ApiSmsAgent {

    @POST
    @Path("/auth/sms-send")
    @Address(ApiAddr.API_AUTH_SMS_SEND)
    JsonObject sendAsync(@BodyParam JsonObject params);

    @Path("/auth/sms-login")
    @POST
    @Address(ApiAddr.API_AUTH_SMS_LOGIN)
    JsonObject login(@BodyParam JsonObject body);
}
