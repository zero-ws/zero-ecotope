package io.zerows.plugins.security.oauth2.server;

import io.r2mo.openapi.components.schemas.OAuth2TokenResponse;
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
import io.zerows.epoch.annotations.Format;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@EndPoint
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public interface CallbackAgent {

    @GET
    @Path("/oauth2/authorized/{clientId}")
    @Format(smart = true, freedom = true)
    @Address(Addr.BACK_CLIENT)
    @Operation(
        summary = DescAuth._oauth2_callback_summary,
        description = DescAuth._oauth2_callback_desc,
        parameters = {
            // Path 参数：注册ID (如 github, google)
            @Parameter(
                name = "clientId",
                description = DescAuth.OAuth2.registrationId,
                in = ParameterIn.PATH,
                required = true,
                example = "github"
            ),
            // Query 参数：成功时的 code
            @Parameter(
                name = "code",
                description = DescAuth.OAuth2.code,
                in = ParameterIn.QUERY,
                example = "a1b2c3d4e5f6..."
            ),
            // Query 参数：状态码 (防 CSRF)
            @Parameter(
                name = "state",
                description = DescAuth.OAuth2.state,
                in = ParameterIn.QUERY,
                required = true,
                example = "xyz123"
            ),
            // Query 参数：失败时的 error
            @Parameter(
                name = "error",
                description = DescAuth.OAuth2.error,
                in = ParameterIn.QUERY,
                example = "access_denied"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // 返回通用的 JsonObject (可能包含 token 或 user 信息)
                    schema = @Schema(implementation = OAuth2TokenResponse.class)
                )
            )
        }
    )
    JsonObject handleCallback(@PathParam("clientId") String registrationId,
                              @QueryParam("code") String code,
                              @QueryParam("state") String state,
                              @QueryParam("error") String error);
}