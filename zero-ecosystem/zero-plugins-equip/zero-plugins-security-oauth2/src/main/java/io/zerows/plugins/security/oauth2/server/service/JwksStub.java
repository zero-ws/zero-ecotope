package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class JwksStub {

    public Future<JsonObject> jwks() {
        final RSAPublicKey pubKey = (RSAPublicKey) OAuth2KeyStore.get().getKeyPair().getPublic();

        final String n = Base64.getUrlEncoder().withoutPadding().encodeToString(pubKey.getModulus().toByteArray());
        final String e = Base64.getUrlEncoder().withoutPadding().encodeToString(pubKey.getPublicExponent().toByteArray());

        final JsonObject jwk = new JsonObject()
            .put("kty", "RSA")
            .put("use", "sig")
            .put("alg", "RS256")
            .put("kid", "zero-oauth2-key-1") // Static KID for this session
            .put("n", n)
            .put("e", e);

        final JsonObject jwks = new JsonObject()
            .put("keys", new JsonArray().add(jwk));

        return Future.succeededFuture(jwks);
    }
}
