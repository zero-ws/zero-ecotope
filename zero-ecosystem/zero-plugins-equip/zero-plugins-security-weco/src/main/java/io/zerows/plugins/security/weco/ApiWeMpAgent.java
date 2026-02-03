package io.zerows.plugins.security.weco;

import io.r2mo.openapi.components.schemas.WeChatQrResponse;
import io.r2mo.openapi.components.schemas.WeChatStatusRequest;
import io.r2mo.openapi.components.schemas.WeChatStatusResponse;
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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.extension.BodyParam;

/**
 * 微信公众号 (WeChat) 认证控制器
 *
 * @author lang : 2025-12-09
 */
@EndPoint
@Tag(name = DescAuth.group)
public interface ApiWeMpAgent {
    // ==========================================
    // 模式二：扫码登录 (PC端/非微信环境使用)
    // ==========================================

    /**
     * 获取微信扫码登录二维码
     * <p>GET /auth/wechat-qrcode</p>
     */
    @GET
    @Path("/auth/wechat-qrcode")
    @Address(ApiAddr.API_AUTH_WEMP_QRCODE)
    @Operation(
        summary = DescAuth._auth_wechat_qrcode_summary, description = DescAuth._auth_wechat_qrcode_desc,
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = WeChatQrResponse.class)
                )
            )
        }
    )
    JsonObject qrCode();


    /**
     * 检查扫码状态
     * <p>POST /auth/wechat-status</p>
     *
     * @param params 请求参数 { "uuid": "..." }
     */
    @POST
    @Path("/auth/wechat-status")
    @Address(ApiAddr.API_AUTH_WEMP_STATUS)
    @Operation(
        summary = DescAuth._auth_wechat_status_summary, description = DescAuth._auth_wechat_status_desc,
        requestBody = @RequestBody(
            required = true, description = DescMeta.request_post,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = WeChatStatusRequest.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = WeChatStatusResponse.class)
                )
            )
        }
    )
    JsonObject status(@BodyParam JsonObject params);
}
