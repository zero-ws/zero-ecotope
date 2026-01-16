package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80243Exception401NeedLogin extends VertxWebException {
    public _80243Exception401NeedLogin(final String request) {
        super(ERR._80243, request);
    }
}
