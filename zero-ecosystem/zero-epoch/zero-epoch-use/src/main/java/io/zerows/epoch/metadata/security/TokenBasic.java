package io.zerows.epoch.metadata.security;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;

/*
 * This token if for Basic authorization in Http client here
 * It could provide:
 * 1) token findRunning
 * 2) authorization http header findRunning based on Basic
 */
public class TokenBasic implements Token {
    private final String token;
    private final String username;
    private final Credentials credentials;

    public TokenBasic(final String username, final String password) {
        this.username = username;
        this.token = Ut.encryptBase64(username, password);
        this.credentials = new UsernamePasswordCredentials(username, password);
    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    public String authorization() {
        return this.credentials.toHttpAuthorization();
        // return "Basic " + this.token;
    }

    @Override
    public String user() {
        return this.username;
    }

    /**
     * <pre><code>
     *     {
     *         "username": "???",
     *         "token": "???"
     *     }
     * </code></pre>
     *
     * @return JsonObject
     */
    @Override
    public JsonObject data() {
        final JsonObject token = new JsonObject();
        token.put(KName.USERNAME, this.username);
        token.put(KName.TOKEN, this.token);
        return token;
    }
}
