package io.zerows.extension.commerce.rbac.agent.api.login;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Codex;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.extension.commerce.rbac.eon.Addr;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.BodyParam;

/*
 * Login Api
 * 1. Provide username/password to access /oauth/login get client_secret field ( Issue when create )
 * 2. Access /oauth/authorize to get authorization code
 * 3. Access /oauth/token to get token
 */
@EndPoint
public interface LoginAgent {

    /*
     * /oauth/login
     *
     * Request:
     * {
     *      username: "lang.yu",
     *      password: "XXX(MD5)",
     *      verifyCode: "When `verifyCode` enabled, here must contains additional part"
     * }
     */
    @POST
    @Path("/oauth/login")
    @Address(Addr.Auth.LOGIN)
    JsonObject login(@BodyParam @Codex JsonObject data);

    /*
     * /oauth/authorize
     *
     * Request:
     * {
     *      client_id: "xxx",
     *      client_secret: "xxx",
     *      response_type: "code",
     *      scope: "xxx"
     * }
     */
    @POST
    @Path("/oauth/authorize")
    @Address(Addr.Auth.AUTHORIZE)
    JsonObject authorize(@BodyParam @Codex JsonObject data);

    /*
     * /oauth/token
     *
     * Request:
     * {
     *      client_id: "xxx",
     *      code: "temp"
     * }
     */
    @POST
    @Path("/oauth/token")
    @Address(Addr.Auth.TOKEN)
    JsonObject token(@BodyParam @Codex JsonObject data);

    // --------------------- Image Code ------------------------


    /*
     * Sigma must be in XHeader for multi application here
     */
    @POST
    @Path("/captcha/image")
    @Address(Addr.Auth.CAPTCHA_IMAGE)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    JsonObject generateImage();

    @POST
    @Path("/captcha/image-verify")
    @Address(Addr.Auth.CAPTCHA_IMAGE_VERIFY)
    JsonObject verifyImage(@BodyParam JsonObject request);

    // --------------------- Ldap Authorization ------------------------
}
