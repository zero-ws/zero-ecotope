package io.zerows.plugins.security.api;

import io.r2mo.openapi.components.schemas.ResponseCaptcha;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

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
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public interface CaptchaAgent {
    /**
     * 获取图片验证码
     * <pre>
     *     {
     *         "captchaId": "????",
     *         "image": "data:image/png;base64,XXXX"
     *     }
     * </pre>
     *
     * @return 响应结果
     */
    @GET
    @Path("/auth/captcha")
    @Address(Addr.API_AUTH_CAPTCHA)
    @Operation(
        summary = DescAuth._auth_captcha_summary, description = DescAuth._auth_captcha_desc,
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseCaptcha.class)
                )
            )
        }
    )
    JsonObject captcha();
}
