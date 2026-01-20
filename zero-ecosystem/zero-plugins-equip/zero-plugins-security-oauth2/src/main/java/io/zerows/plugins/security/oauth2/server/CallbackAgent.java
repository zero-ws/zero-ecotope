package io.zerows.plugins.security.oauth2.server;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Format;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@EndPoint
public interface CallbackAgent {

    @GET
    @Path("/oauth2/authorized/{clientId}")
    @Format(smart = true, freedom = true)
    @Address(Addr.BACK_CLIENT)
    JsonObject handleCallback(@PathParam("clientId") String registrationId,
                              @QueryParam("code") String code,
                              @QueryParam("state") String state,
                              @QueryParam("error") String error);
}
