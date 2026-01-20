package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.oauth2.OAuth2ServerActor;

public class MetaService implements MetaStub {
    @Override
    public Future<JsonObject> jwksAsync() {
        final JsonObject keystoreJ = OAuth2ServerActor.keystoreOf();
        return Future.succeededFuture(keystoreJ);
    }
}
