package io.zerows.plugins.security.weco;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Redirect;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@EndPoint
public interface ApiComAgent {

    @GET
    @Path("/auth/wecom-init")
    @Address(ApiAddr.API_AUTH_WECOM_INIT)
    String init(@QueryParam("targetUrl") String targetUrl);

    @GET
    @Path("/auth/wecom-login")
    @Address(ApiAddr.API_AUTH_WECOM_LOGIN)
    @Redirect
    String login(@QueryParam("code") String code, @QueryParam("state") String state);

    @GET
    @Path("/auth/wecom-qrcode")
    @Address(ApiAddr.API_AUTH_WECOM_QRCODE)
    JsonObject qrCode(@QueryParam("state") String state);
}
