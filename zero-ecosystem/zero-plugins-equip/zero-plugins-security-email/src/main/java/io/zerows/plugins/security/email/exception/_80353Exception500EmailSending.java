package io.zerows.plugins.security.email.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80353Exception500EmailSending extends VertxWebException {
    public _80353Exception500EmailSending(final String email) {
        super(ERR._80353, email);
    }
}
