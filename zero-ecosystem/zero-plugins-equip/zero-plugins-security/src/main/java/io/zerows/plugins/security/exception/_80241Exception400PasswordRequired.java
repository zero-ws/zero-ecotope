package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80241Exception400PasswordRequired extends VertxWebException {
    public _80241Exception400PasswordRequired(final String password) {
        super(ERR._80241, password);
    }
}
