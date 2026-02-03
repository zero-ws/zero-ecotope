package io.zerows.plugins.security.sms;

import io.r2mo.openapi.components.schemas.RequestSmsLogin;
import io.r2mo.openapi.components.schemas.RequestSmsSend;
import io.r2mo.openapi.components.schemas.ResponseLoginDynamic;
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

@EndPoint
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public interface ApiSmsAgent {

    @POST
    @Path("/auth/sms-send")
    @Address(ApiAddr.API_AUTH_SMS_SEND)
    @Operation(
        summary = DescAuth._auth_sms_send_summary, description = DescAuth._auth_sms_send_desc,
        requestBody = @RequestBody(
            required = true, description = DescMeta.request_post,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RequestSmsSend.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                        name = "data",
                        type = "boolean",
                        description = DescMeta.response_ok_boolean
                    )
                )
            )
        }
    )
    JsonObject sendAsync(@BodyParam JsonObject params);

    @Path("/auth/sms-login")
    @POST
    @Address(ApiAddr.API_AUTH_SMS_LOGIN)
    @Operation(
        summary = DescAuth._auth_sms_login_summary, description = DescAuth._auth_sms_login_desc,
        requestBody = @RequestBody(
            required = true, description = DescMeta.request_post,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = RequestSmsLogin.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseLoginDynamic.class)
                )
            )
        }
    )
    JsonObject login(@BodyParam JsonObject body);
}
