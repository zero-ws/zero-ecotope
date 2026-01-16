package io.zerows.sdk.security;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.ext.web.handler.AuthenticationHandler;

public interface WallHandler extends AuthenticationHandler {

    @Fluent
    WallHandler add(AuthenticationHandler handler);
}
