package io.zerows.plugins.security.oauth2.server;

import io.r2mo.openapi.components.schemas.OAuth2IntrospectRequest;
import io.r2mo.openapi.components.schemas.OAuth2IntrospectResponse;
import io.r2mo.openapi.components.schemas.OAuth2JwksResponse;
import io.r2mo.openapi.components.schemas.OAuth2RevokeRequest;
import io.r2mo.openapi.components.schemas.OAuth2TokenRequest;
import io.r2mo.openapi.components.schemas.OAuth2TokenResponse;
import io.r2mo.openapi.components.schemas.OAuth2UserinfoResponse;
import io.r2mo.openapi.operations.DescAuth;
import io.r2mo.openapi.operations.DescMeta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.annotations.Format;
import io.zerows.epoch.annotations.Redirect;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@EndPoint
@Tag(
    name = DescAuth.group,
    description = DescAuth.description
)
public interface OAuth2Agent {

    // -------------------------------------------------------------------------
    // 1. Authorize Endpoint (GET Redirect)
    // -------------------------------------------------------------------------
    @GET
    @Path(AddrApi.OAUTH2_AUTHORIZE)
    @Address(Addr.AUTHORIZE)
    @Redirect
    @Format(smart = true, freedom = true)
    @Operation(
        summary = DescAuth._oauth2_authorize_summary,
        description = DescAuth._oauth2_authorize_desc,
        parameters = {
            @Parameter(name = "response_type", description = DescAuth.OAuth2.response_type, in = ParameterIn.QUERY, required = true, example = "code"),
            @Parameter(name = "client_id", description = DescAuth.OAuth2.client_id, in = ParameterIn.QUERY, required = true),
            @Parameter(name = "redirect_uri", description = DescAuth.OAuth2.redirect_uri, in = ParameterIn.QUERY, required = true),
            @Parameter(name = "scope", description = DescAuth.OAuth2.scope, in = ParameterIn.QUERY),
            @Parameter(name = "state", description = DescAuth.OAuth2.state, in = ParameterIn.QUERY)
        },
        responses = {
            @ApiResponse(responseCode = DescMeta.response_code_302)
        }
    )
    JsonObject authorize(
        @QueryParam("response_type") String responseType,
        @QueryParam("client_id") String clientId,
        @QueryParam("redirect_uri") String redirectUri,
        @QueryParam("scope") String scope,
        @QueryParam("state") String state
    );

    // -------------------------------------------------------------------------
    // 2. Token Endpoint (POST Form)
    // -------------------------------------------------------------------------
    @POST
    @Path(AddrApi.OAUTH2_TOKEN)
    @Address(Addr.TOKEN)
    @Format(smart = true, freedom = true)
    @Operation(
        summary = DescAuth._oauth2_token_summary,
        description = DescAuth._oauth2_token_desc,
        parameters = {
            @Parameter(name = "Authorization", description = DescAuth.OAuth2.authorization, in = ParameterIn.HEADER)
        },
        requestBody = @RequestBody(
            description = DescMeta.request_post,
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_FORM_URLENCODED,
                // ✅ 使用之前定义的 OAuth2TokenRequest Schema
                schema = @Schema(implementation = OAuth2TokenRequest.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // ✅ 使用 OAuth2TokenResponse Schema
                    schema = @Schema(implementation = OAuth2TokenResponse.class)
                )
            )
        }
    )
    JsonObject token(
        @FormParam("grant_type") String grantType,
        @FormParam("scope") String scope,
        @FormParam("code") String code,
        @FormParam("redirect_uri") String redirectUri,
        @FormParam("code_verifier") String codeVerifier,
        @FormParam("client_id") String clientId,
        @FormParam("client_secret") String clientSecret,
        @HeaderParam("Authorization") String authorization,
        @FormParam("refresh_token") String refreshToken,
        @FormParam("username") String username,
        @FormParam("password") String password
    );

    // -------------------------------------------------------------------------
    // 3. JWKS Endpoint (GET JSON)
    // -------------------------------------------------------------------------
    @GET
    @Path(AddrApi.OAUTH2_JWKS)
    @Address(Addr.JWKS)
    @Format(freedom = true)
    @Operation(
        summary = DescAuth._oauth2_jwks_summary,
        description = DescAuth._oauth2_jwks_desc,
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                description = DescMeta.response_ok_json,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // ✅ 使用 OAuth2JwksResponse Schema
                    schema = @Schema(implementation = OAuth2JwksResponse.class)
                )
            )
        }
    )
    JsonObject jwks();

    // -------------------------------------------------------------------------
    // 4. Revoke Endpoint (POST Form)
    // -------------------------------------------------------------------------
    @POST
    @Path(AddrApi.OAUTH2_REVOKE)
    @Address(Addr.REVOKE)
    @Format(freedom = true, smart = true)
    @Operation(
        summary = DescAuth._oauth2_revoke_summary,
        description = DescAuth._oauth2_revoke_desc,
        parameters = {
            @Parameter(name = "Authorization", description = DescAuth.OAuth2.authorization, in = ParameterIn.HEADER)
        },
        requestBody = @RequestBody(
            description = DescMeta.request_post,
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_FORM_URLENCODED,
                // ✅ 使用 OAuth2RevokeRequest Schema
                schema = @Schema(implementation = OAuth2RevokeRequest.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200
            )
        }
    )
    JsonObject revoke(@HeaderParam("Authorization") String authorization,
                      @FormParam("token") String token);

    // -------------------------------------------------------------------------
    // 5. Introspect Endpoint (POST Form)
    // -------------------------------------------------------------------------
    @POST
    @Path(AddrApi.OAUTH2_INTROSPECT)
    @Address(Addr.INTROSPECT)
    @Format(freedom = true, smart = true)
    @Operation(
        summary = DescAuth._oauth2_introspect_summary,
        description = DescAuth._oauth2_introspect_desc,
        parameters = {
            @Parameter(name = "Authorization", description = DescAuth.OAuth2.authorization, in = ParameterIn.HEADER)
        },
        requestBody = @RequestBody(
            description = DescMeta.request_post,
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_FORM_URLENCODED,
                // ✅ 使用 OAuth2IntrospectRequest Schema
                schema = @Schema(implementation = OAuth2IntrospectRequest.class)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // ✅ 使用 OAuth2IntrospectResponse Schema
                    schema = @Schema(implementation = OAuth2IntrospectResponse.class)
                )
            )
        }
    )
    JsonObject introspect(@HeaderParam("Authorization") String authorization,
                          @FormParam("token") String token);

    // -------------------------------------------------------------------------
    // 6. UserInfo Endpoint (GET JSON)
    // -------------------------------------------------------------------------
    @GET
    @Path(AddrApi.OAUTH2_USERINFO)
    @Address(Addr.USERINFO)
    @Format(freedom = true)
    @Operation(
        summary = DescAuth._oauth2_userinfo_summary,
        description = DescAuth._oauth2_userinfo_desc,
        parameters = {
            @Parameter(
                name = "Authorization",
                description = "Bearer {access_token}",
                in = ParameterIn.HEADER,
                required = true,
                example = "Bearer eyJhbGciOiJIUz..."
            )
        },
        responses = {
            @ApiResponse(
                responseCode = DescMeta.response_code_200,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    // ✅ 使用 OAuth2UserinfoResponse Schema
                    schema = @Schema(implementation = OAuth2UserinfoResponse.class)
                )
            )
        }
    )
    JsonObject userinfo(@HeaderParam("Authorization") String authorization);
}