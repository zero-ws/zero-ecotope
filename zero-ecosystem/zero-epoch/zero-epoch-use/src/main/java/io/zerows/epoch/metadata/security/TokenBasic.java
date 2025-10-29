package io.zerows.epoch.metadata.security;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.zerows.sdk.security.Token;

public class TokenBasic implements Token {
    private final Credentials credentials;

    public TokenBasic(final String username, final String password) {
        this.credentials = new UsernamePasswordCredentials(username, password);
    }

    @Override
    public Credentials credentials() {
        return this.credentials;
    }

    @Override
    public String authorization() {
        return this.credentials.toHttpAuthorization();
    }

    @Override
    public String user() {
        return ((UsernamePasswordCredentials) this.credentials).getUsername();
    }

    @Override
    public JsonObject data() {
        return this.credentials.toJson();
    }
}
