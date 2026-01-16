package io.zerows.plugins.security.service;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.vertx.core.Future;

public interface AuthLoginStub {

    Future<UserAt> login(LoginRequest request);
}
