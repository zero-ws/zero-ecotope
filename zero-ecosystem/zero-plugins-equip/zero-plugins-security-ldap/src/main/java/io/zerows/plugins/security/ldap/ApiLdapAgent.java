package io.zerows.plugins.security.ldap;

import io.r2mo.openapi.components.schemas.security.RequestLoginCommon;
import io.r2mo.openapi.components.schemas.security.ResponseLoginDynamic;
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
public interface ApiLdapAgent {

    @POST
    @Path("/auth/ldap-login")
    @Address(ApiAddr.API_AUTH_LDAP_LOGIN)
    @Operation(
        summary = DescAuth._auth_ldap_login_summary, description = DescAuth._auth_ldap_login_description,
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
                description = DescMeta.response_success,
                /*
                 * - data
                 *   - id
                 *   - token
                 *   - refreshToken
                 */
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(name = "data", implementation = ResponseLoginDynamic.class)
                )
            )
        }
    )
    JsonObject login(@BodyParam JsonObject body);
}
