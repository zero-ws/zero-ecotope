package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80240Exception400UsernameRequired extends VertxWebException {
    public _80240Exception400UsernameRequired(final String username) {
        super(ERR._80240, username);
    }
}
