package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
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

    public Future<JsonObject> response() {
        return SecuritySession.of().authorized401(this.userAt, this.getToken())
            .map(user -> this.responseData());
    }

    protected abstract JsonObject responseData();
}
