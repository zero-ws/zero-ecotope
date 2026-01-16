package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AsyncLoginResponse extends LoginResponse {

    public AsyncLoginResponse() {
        super();
    }

    public AsyncLoginResponse(final UserAt userAt) {
        super(userAt);
    }

    public abstract Future<JsonObject> response();
}
