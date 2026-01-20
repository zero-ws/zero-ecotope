package io.zerows.plugins.security.oauth2.server.previous;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TokenManageStub {

    // Simple in-memory revocation list
    private static final ConcurrentMap<String, Boolean> REVOKED_TOKENS = new ConcurrentHashMap<>();

    public Future<JsonObject> revoke(final JsonObject body) {
        final String token = body.getString("token");
        if (token != null) {
            REVOKED_TOKENS.put(token, Boolean.TRUE);
        }
        return Future.succeededFuture(new JsonObject());
    }

    public Future<JsonObject> introspect(final JsonObject body) {
        final String token = body.getString("token");
        if (token == null) {
            return Future.succeededFuture(new JsonObject().put("active", false));
        }

        // 1. Check Revocation
        if (REVOKED_TOKENS.containsKey(token)) {
            return Future.succeededFuture(new JsonObject().put("active", false));
        }

        // 2. Validate Token
        final JWTAuth provider = OAuth2KeyStore.get().getProvider();
        return provider.authenticate(new TokenCredentials(token))
            .map(user -> {
                final JsonObject principal = user.principal();
                return new JsonObject()
                    .put("active", true)
                    .put("sub", principal.getString("sub"))
                    .put("scope", principal.getString("scope"))
                    .put("exp", principal.getLong("exp"))
                    .put("iat", principal.getLong("iat"));
            })
            .otherwise(t -> new JsonObject().put("active", false));
    }

    public Future<JsonObject> userinfo(final String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Future.failedFuture("Missing or invalid Authorization header");
        }

        final String token = authorization.substring(7);
        if (REVOKED_TOKENS.containsKey(token)) {
            return Future.failedFuture("Token revoked");
        }

        final JWTAuth provider = OAuth2KeyStore.get().getProvider();
        return provider.authenticate(new TokenCredentials(token))
            .map(User::principal);
    }
}
