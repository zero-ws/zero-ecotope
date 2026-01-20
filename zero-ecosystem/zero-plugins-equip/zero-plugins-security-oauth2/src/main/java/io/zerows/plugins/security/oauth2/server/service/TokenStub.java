package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.oauth2.metadata.OAuth2Credential;

public interface TokenStub {

    /**
     * 令牌端点 (RFC 6749)
     * POST /oauth2/token
     */
    Future<JsonObject> tokenAsync(JsonObject request);

    /**
     * 令牌撤销端点 (RFC 7009)
     * POST /oauth2/revoke
     */
    Future<JsonObject> revokeAsync(JsonObject request, OAuth2Credential credential);

    /**
     * 令牌内省端点 (RFC 7662)
     * POST /oauth2/introspect
     */
    Future<JsonObject> introspectAsync(String token, OAuth2Credential credential);
}