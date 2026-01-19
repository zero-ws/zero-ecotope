package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80255Exception401TokenType extends VertxWebException {
    public _80255Exception401TokenType(final String input, final String required) {
        super(ERR._80255, input, required);
    }
}
