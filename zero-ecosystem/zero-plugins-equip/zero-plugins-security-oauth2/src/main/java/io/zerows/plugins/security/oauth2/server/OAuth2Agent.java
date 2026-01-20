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
    @Format(smart = true, freedom = true)
    JsonObject token(
        // ---------------------------------------------------------
        // 1. 核心参数 (Core)
        // ---------------------------------------------------------
        @FormParam("grant_type") String grantType,
        @FormParam("scope") String scope,

        // ---------------------------------------------------------
        // 2. 授权码模式 & PKCE (Authorization Code)
        // ---------------------------------------------------------
        @FormParam("code") String code,
        @FormParam("redirect_uri") String redirectUri,
        @FormParam("code_verifier") String codeVerifier, // PKCE 专用

        // ---------------------------------------------------------
        // 3. 客户端身份 (Client Identity)
        // 支持 Body 传参 (client_id, client_secret)
        // 也支持 Header 传参 (Authorization: Basic ...)
        // ---------------------------------------------------------
        @FormParam("client_id") String clientId,
        @FormParam("client_secret") String clientSecret,
        @HeaderParam("Authorization") String authorization,

        // ---------------------------------------------------------
        // 4. 刷新令牌模式 (Refresh Token)
        // ---------------------------------------------------------
        @FormParam("refresh_token") String refreshToken,

        // ---------------------------------------------------------
        // 5. 密码模式 (Password - 仅作兼容，现代不推荐)
        // ---------------------------------------------------------
        @FormParam("username") String username,
        @FormParam("password") String password
    );

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
