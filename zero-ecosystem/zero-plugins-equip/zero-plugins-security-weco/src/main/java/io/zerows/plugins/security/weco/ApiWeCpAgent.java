package io.zerows.plugins.security.weco;

import io.r2mo.openapi.components.schemas.WeComInitResponse;
import io.r2mo.openapi.components.schemas.WeComQrResponse;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Redirect;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@EndPoint
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public interface ApiWeCpAgent {

    // 1. 初始化接口 (返回 JSON state)
    // 虽然签名是 String，但根据上下文这里返回的是 JSON 格式的字符串
    @GET
    @Path("/auth/wecom-init")
    @Address(ApiAddr.API_AUTH_WECOM_INIT)
    @Operation(
        summary = DescAuth._auth_wecom_init_summary,
        description = DescAuth._auth_wecom_init_desc,
        parameters = {
            @Parameter(
                name = "targetUrl",
                description = DescAuth.P.targetUrl,
                in = ParameterIn.QUERY,
                example = "https://console.r2mo.io"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // 关联之前定义的 Response 结构
                    schema = @Schema(name = "data", implementation = WeComInitResponse.class)
                )
            )
        }
    )
    String init(@QueryParam("targetUrl") String targetUrl);

    // 2. 登录回调接口 (重定向)
    // @Redirect 意味着返回的是 302 Found，且 Header 中包含 Location
    @GET
    @Path("/auth/wecom-login")
    @Address(ApiAddr.API_AUTH_WECOM_LOGIN)
    @Redirect
    @Operation(
        summary = DescAuth._auth_wecom_login_summary,
        description = DescAuth._auth_wecom_login_desc,
        parameters = {
            @Parameter(
                name = "code",
                description = DescAuth.P.code,
                in = ParameterIn.QUERY,
                required = true,
                example = "ww_auth_code_123456"
            ),
            @Parameter(
                name = "state",
                description = DescAuth.P.state,
                in = ParameterIn.QUERY,
                required = true,
                example = "e1eea04ed597465c833418d4cdc9373b"
            )
        },
        responses = {
            @ApiResponse(
                // ✅ 重点：标注为 302 重定向
                responseCode = DescMeta.response_code_302,
                description = DescAuth.P.targetUrl,
                content = @Content(
                    // 这里的 String 是重定向的目标 URL，通常表现为纯文本或 HTML
                    mediaType = MediaType.TEXT_HTML,
                    schema = @Schema(
                        type = "string",
                        example = "https://console.r2mo.io/dashboard?token=eyJhbGciOiJIUz..."
                    )
                )
            )
        }
    )
    String login(@QueryParam("code") String code, @QueryParam("state") String state);

    // 3. 获取二维码配置 (返回 JSON)
    @GET
    @Path("/auth/wecom-qrcode")
    @Address(ApiAddr.API_AUTH_WECOM_QRCODE)
    @Operation(
        summary = DescAuth._auth_wecom_qrcode_summary,
        description = DescAuth._auth_wecom_qrcode_desc,
        parameters = {
            @Parameter(
                name = "state",
                description = DescAuth.P.state,
                in = ParameterIn.QUERY,
                required = true,
                example = "e1eea04ed597465c833418d4cdc9373b"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // 关联之前定义的 Response 结构
                    schema = @Schema(name = "data", implementation = WeComQrResponse.class)
                )
            )
        }
    )
    JsonObject qrCode(@QueryParam("state") String state);
}