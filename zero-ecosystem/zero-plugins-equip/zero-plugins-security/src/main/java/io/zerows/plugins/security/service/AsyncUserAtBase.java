package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;

public abstract class AsyncUserAtBase implements AsyncUserAt {
    @Override
    public Future<UserAt> loadLogged(final LoginRequest request) {
        return null;
    }

    @Override
    public Future<UserAt> loadLogged(final String identifier) {
        return null;
    }
}
