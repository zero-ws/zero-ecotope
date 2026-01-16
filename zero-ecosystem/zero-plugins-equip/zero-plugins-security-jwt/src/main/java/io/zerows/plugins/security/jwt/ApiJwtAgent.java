package io.zerows.plugins.security.jwt;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
public interface ApiJwtAgent {
    
    @POST
    @Path("/auth/jwt-login")
    @Address(ApiAddr.API_AUTH_JWT_LOGIN)
    JsonObject login(@BodyParam JsonObject body);
}
