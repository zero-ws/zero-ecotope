package io.zerows.plugins.security.jwt;

import io.r2mo.openapi.components.schemas.RequestLoginCommon;
import io.r2mo.openapi.components.schemas.ResponseLoginJwt;
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
@Tag(name = DescAuth.group)
public interface ApiJwtAgent {

    @POST
    @Path("/auth/jwt-login")
    @Address(ApiAddr.API_AUTH_JWT_LOGIN)
    @Operation(
        summary = DescAuth._auth_jwt_login_summary, description = DescAuth._auth_jwt_login_desc,
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
                 *   - refreshToken
                 *   - tokenType
                 *   - expiresIn
                 */
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseLoginJwt.class)
                )
            )
        }
    )
    JsonObject login(@BodyParam JsonObject body);
}
