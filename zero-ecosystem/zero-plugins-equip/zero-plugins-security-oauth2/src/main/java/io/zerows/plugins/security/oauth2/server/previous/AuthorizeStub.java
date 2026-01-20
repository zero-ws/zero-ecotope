package io.zerows.plugins.security.oauth2.server.previous;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AuthorizeStub {

    // Code Store: Key = Code, Value = Data
    private static final ConcurrentMap<String, JsonObject> CODE_STORE = new ConcurrentHashMap<>();
    private static final long EXPIRE_MS = 600000; // 10 minutes

    public static JsonObject consume(final String code) {
        if (code == null) {
            return null;
        }
        final JsonObject data = CODE_STORE.get(code);
        if (data == null) {
            return null;
        }
        // Remove code (Consumable once)
        CODE_STORE.remove(code);

        // Check expiry
        final long expiredAt = data.getLong("expired_at", 0L);
        if (System.currentTimeMillis() > expiredAt) {
            return null;
        }
        return data;
    }

    public Future<JsonObject> authorize(final String responseType, final String clientId, final String redirectUri, final String scope, final String state) {
        // 1. Basic Validation
        if (clientId == null || clientId.isEmpty()) {
            return Future.failedFuture("Invalid client_id");
        }
        if (!"code".equals(responseType)) {
            // For now only support response_type=code
            return Future.failedFuture("Unsupported response_type: " + responseType);
        }

        // 2. Generate Authorization Code
        // Use UUID for simplicity
        final String code = UUID.randomUUID().toString();

        // 3. Store Code with metadata
        final JsonObject authData = new JsonObject()
            .put("client_id", clientId)
            .put("redirect_uri", redirectUri)
            .put("scope", scope)
            .put("state", state)
            .put("user", "mock-user-id") // In real flow, this comes from authenticated user session
            .put("expired_at", System.currentTimeMillis() + EXPIRE_MS);

        CODE_STORE.put(code, authData);

        // 4. Return result
        final JsonObject response = new JsonObject()
            .put("code", code)
            .put("state", state);

        return Future.succeededFuture(response);
    }
}
