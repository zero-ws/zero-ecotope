package io.zerows.extension.module.rbac.api;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Codex;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 新版基础登录接口定义
 * <pre>
 *     1. 基础账号登录
 *        - POST /auth/login
 *     2. 启用图片验证码
 *        - GET /auth/captcha
 * </pre>
 */
@EndPoint
public interface AuthLoginAgent {

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
//    @POST
//    @Path("/oauth/login")
//    @Address(AddrAuth.LOGIN)
//    JsonObject login(@BodyParam @Codex JsonObject data);

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
//    @POST
//    @Path("/captcha/image")
//    @Address(Addr.Auth.CAPTCHA_IMAGE)
//    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
//    @Produces(MediaType.APPLICATION_OCTET_STREAM)
//    JsonObject generateImage();

    @POST
    @Path("/captcha/image-verify")
    @Address(Addr.Auth.CAPTCHA_IMAGE_VERIFY)
    JsonObject verifyImage(@BodyParam JsonObject request);

    // --------------------- Ldap Authorization ------------------------
}
