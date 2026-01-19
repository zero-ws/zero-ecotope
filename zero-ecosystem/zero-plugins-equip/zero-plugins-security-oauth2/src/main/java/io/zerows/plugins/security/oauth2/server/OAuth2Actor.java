package io.zerows.plugins.security.oauth2.server;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.oauth2.server.service.AuthorizeStub;
import io.zerows.plugins.security.oauth2.server.service.JwksStub;
import io.zerows.plugins.security.oauth2.server.service.TokenManageStub;
import io.zerows.plugins.security.oauth2.server.service.TokenStub;
import jakarta.inject.Inject;

@Queue
public class OAuth2Actor {

    @Inject
    private AuthorizeStub authorizeStub;
    @Inject
    private TokenStub tokenStub;
    @Inject
    private JwksStub jwksStub;
    @Inject
    private TokenManageStub tokenManageStub;

    @Address(Addr.AUTHORIZE)
    public Future<JsonObject> authorize(final String responseType, final String clientId, final String redirectUri, final String scope, final String state) {
        return this.authorizeStub.authorize(responseType, clientId, redirectUri, scope, state);
    }

    @Address(Addr.TOKEN)
    public Future<JsonObject> token(final JsonObject body) {
        return this.tokenStub.token(body);
    }

    @Address(Addr.JWKS)
    public Future<JsonObject> jwks(final JsonObject params) {
        return this.jwksStub.jwks();
    }

    @Address(Addr.REVOKE)
    public Future<JsonObject> revoke(final JsonObject body) {
        return this.tokenManageStub.revoke(body);
    }

    @Address(Addr.INTROSPECT)
    public Future<JsonObject> introspect(final JsonObject body) {
        return this.tokenManageStub.introspect(body);
    }

    @Address(Addr.USERINFO)
    public Future<JsonObject> userinfo(final String authorization) {
        return this.tokenManageStub.userinfo(authorization);
    }

}
