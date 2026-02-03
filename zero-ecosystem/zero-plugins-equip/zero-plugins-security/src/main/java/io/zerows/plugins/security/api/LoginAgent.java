package io.zerows.plugins.security.api;

import io.r2mo.openapi.components.schemas.RequestLoginCommon;
import io.r2mo.openapi.components.schemas.ResponseLoginCommon;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 安全插件标准化基础接口
 * <pre>
 *     1. Basic认证无需登录
 *     2. 内置账号登录
 *        POST /auth/login
 *     3. 图片验证码
 *        GET  /auth/captcha
 *     4. 图片验证码直接在登录接口中可直接追加使用（启用禁用取决于配置项）
 * </pre>
 */
@EndPoint
@Tag(name = DescAuth.group, description = DescAuth.description)
public interface LoginAgent {
    @POST
    @Path("/auth/login")
    @Address(Addr.API_AUTH_LOGIN)
    @Operation(
        summary = DescAuth._auth_login_summary, description = DescAuth._auth_login_desc,
        requestBody = @RequestBody(
            required = true, description = DescMeta.request_post,
            /*
             * - username
             * - password
             * - captcha              (optional)
             * - captchaId            (optional)
             */
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RequestLoginCommon.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                /*
                 * - data
                 *   - token
                 *   - id
                 *   - username
                 */
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseLoginCommon.class)
                )
            )
        }
    )
    JsonObject login(@BodyParam JsonObject body);
}
