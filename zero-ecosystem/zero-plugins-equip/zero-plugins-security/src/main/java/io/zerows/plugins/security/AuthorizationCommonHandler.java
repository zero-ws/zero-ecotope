package io.zerows.plugins.security;

import io.vertx.ext.auth.authorization.AuthorizationContext;
import io.vertx.ext.auth.authorization.AuthorizationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthorizationHandler;

import java.util.function.BiConsumer;

/**
 * @author lang : 2025-10-30
 */
class AuthorizationCommonHandler implements AuthorizationHandler {

    @Override
    public AuthorizationHandler addAuthorizationProvider(final AuthorizationProvider authorizationProvider) {
        return null;
    }

    @Override
    public AuthorizationHandler variableConsumer(final BiConsumer<RoutingContext, AuthorizationContext> biConsumer) {
        return null;
    }

    @Override
    public void handle(final RoutingContext routingContext) {

    }
}
