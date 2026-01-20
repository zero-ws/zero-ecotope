package io.zerows.plugins.security.oauth2.server;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Format;
import jakarta.ws.rs.*;
import jakarta.ws.rs.extension.BodyParam;

@EndPoint
public interface OAuth2Agent {

    @GET
    @Path("/oauth2/authorize")
    @Address(Addr.AUTHORIZE)
    JsonObject authorize(
        @QueryParam("response_type") String responseType,
        @QueryParam("client_id") String clientId,
        @QueryParam("redirect_uri") String redirectUri,
        @QueryParam("scope") String scope,
        @QueryParam("state") String state
    );

    @POST
    @Path("/oauth2/token")
    @Address(Addr.TOKEN)
    JsonObject token(@BodyParam JsonObject body);

    @GET
    @Path("/oauth2/jwks")
    @Address(Addr.JWKS)
    @Format(freedom = true)
    JsonObject jwks();

    @POST
    @Path("/oauth2/revoke")
    @Address(Addr.REVOKE)
    JsonObject revoke(@BodyParam JsonObject body);

    @POST
    @Path("/oauth2/introspect")
    @Address(Addr.INTROSPECT)
    JsonObject introspect(@BodyParam JsonObject body);

    @GET
    @Path("/userinfo")
    @Address(Addr.USERINFO)
    JsonObject userinfo(@HeaderParam("Authorization") String authorization);
}
