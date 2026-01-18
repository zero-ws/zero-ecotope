package io.zerows.plugins.security.email.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80352Exception400EmailFormat extends VertxWebException {
    public _80352Exception400EmailFormat(final String email) {
        super(ERR._80352, email);
    }
}
