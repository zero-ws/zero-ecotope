package io.zerows.plugins.security.basic;

import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.handler.AuthenticationHandler;

public interface BasicAuthAdvHandler extends AuthenticationHandler {

    /**
     * The default realm to use
     */
    String DEFAULT_REALM = "vertx-web";

    /**
     * Create a basic auth handler
     *
     * @param authProvider the auth provider to use
     * @return the auth handler
     */
    static BasicAuthAdvHandler create(final AuthenticationProvider authProvider) {
        return new BasicAuthAdvHandlerImpl(authProvider, DEFAULT_REALM);
    }

    /**
     * Create a basic auth handler, specifying realm
     *
     * @param authProvider the auth service to use
     * @param realm        the realm to use
     * @return the auth handler
     */
    static BasicAuthAdvHandler create(final AuthenticationProvider authProvider, final String realm) {
        return new BasicAuthAdvHandlerImpl(authProvider, realm);
    }
}

