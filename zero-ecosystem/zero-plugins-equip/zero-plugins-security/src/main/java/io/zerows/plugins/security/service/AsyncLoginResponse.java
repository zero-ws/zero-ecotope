package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.vertx.common.cache.AkkaOr;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KRef;
import io.zerows.plugins.security.SecuritySession;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AsyncLoginResponse extends LoginResponse {
    protected UserAt userAt;

    public AsyncLoginResponse() {
        super();
    }

    public AsyncLoginResponse(final UserAt userAt) {
        super(userAt);
        this.userAt = userAt;
    }

    private Future<JsonObject> replyAsync(final JsonObject response) {
        return SecuritySession.of().authorized401(this.userAt, this.getToken())
            .map(v -> response);
    }

    public abstract Akka<String> getTokenAsync();

    public Akka<String> getTokenRefresh() {
        return AkkaOr.of();
    }

    public abstract Future<JsonObject> replyToken(final String token, final String refreshToken);

    public Future<JsonObject> response() {
        final KRef ref = new KRef();
        return this.getTokenAsync().<Future<String>>a()
            .compose(ref::future)
            .compose(token -> this.getTokenRefresh().<Future<String>>a())
            .compose(refreshToken -> this.replyToken(ref.get(), refreshToken))
            .compose(this::replyAsync);
    }

    @Override
    public String getToken(final UserAt userAt) {
        return null;
    }
}
