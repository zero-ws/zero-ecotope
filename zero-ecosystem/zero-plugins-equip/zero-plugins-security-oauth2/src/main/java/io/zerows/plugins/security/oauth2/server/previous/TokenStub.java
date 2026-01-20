package io.zerows.plugins.security.oauth2.server.previous;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;

import java.util.UUID;

public class TokenStub {

    public Future<JsonObject> token(final JsonObject body) {
        final String grantType = body.getString("grant_type");

        // 1. Grant Type: authorization_code
        if ("authorization_code".equals(grantType)) {
            final String code = body.getString("code");
            final JsonObject authData = AuthorizeStub.consume(code);

            if (authData == null) {
                return Future.failedFuture("Invalid or expired authorization code");
            }

            // In strict OAuth2, we should also verify redirect_uri matching
            final String redirectUri = body.getString("redirect_uri");
            if (redirectUri != null && !redirectUri.equals(authData.getString("redirect_uri"))) {
                return Future.failedFuture("Redirect URI mismatch");
            }

            return this.generateToken(
                authData.getString("client_id"),
                authData.getString("scope", "default"),
                authData.getString("user")
            );
        }

        // 2. Grant Type: client_credentials
        if ("client_credentials".equals(grantType)) {
            // Mock client authentication (Assume client is valid if request reached here)
            final String clientId = body.getString("client_id");
            if (clientId == null) {
                return Future.failedFuture("Missing client_id");
            }

            return this.generateToken(clientId, body.getString("scope", "default"), null);
        }

        return Future.failedFuture("Unsupported grant_type: " + grantType);
    }

    private Future<JsonObject> generateToken(final String clientId, final String scope, final String userId) {
        final JWTAuth provider = OAuth2KeyStore.get().getProvider();

        // Access Token
        final JsonObject accessPayload = new JsonObject()
            .put("sub", clientId)
            .put("scope", scope);

        if (userId != null) {
            accessPayload.put("user_id", userId);
        }

        // Sign Access Token (1 hour)
        final String accessToken = provider.generateToken(
            accessPayload,
            new JWTOptions().setExpiresInSeconds(3600)
        );

        // Refresh Token (UUID for simplicity in this implementation)
        final String refreshToken = UUID.randomUUID().toString();

        final JsonObject response = new JsonObject()
            .put("access_token", accessToken)
            .put("token_type", "Bearer")
            .put("expires_in", 3600)
            .put("refresh_token", refreshToken)
            .put("scope", scope);

        return Future.succeededFuture(response);
    }
}
