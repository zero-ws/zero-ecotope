package io.zerows.plugins.security;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.CredentialValidationException;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.platform.enums.SecurityType;

/**
 * @author lang : 2025-10-30
 */
public class SecurityCredentials implements Credentials {
    private final SecurityMeta meta;
    private final Credentials delegate;

    SecurityCredentials(final SecurityMeta meta, final User user) {
        this.meta = meta;
        final SecurityType type = meta.getType();
        if (SecurityType.BASIC == type) {
            this.delegate = new UsernamePasswordCredentials(user.principal());
        } else {
            this.delegate = new TokenCredentials(user.principal());
        }
    }

    @Override
    public <V> void checkValid(final V arg) throws CredentialValidationException {
        this.delegate.checkValid(arg);
    }

    @Override
    public JsonObject toJson() {
        return this.delegate.toJson();
    }

    @Override
    public Credentials applyHttpChallenge(final String challenge,
                                          final HttpMethod method, final String uri,
                                          final Integer nc, final String cnonce) throws CredentialValidationException {
        return this.delegate.applyHttpChallenge(challenge, method, uri, nc, cnonce);
    }

    @Override
    public String toHttpAuthorization() {
        return this.delegate.toHttpAuthorization();
    }
}
