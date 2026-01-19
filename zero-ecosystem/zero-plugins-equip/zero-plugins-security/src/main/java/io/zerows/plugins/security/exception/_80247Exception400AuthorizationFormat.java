package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80247Exception400AuthorizationFormat extends VertxWebException {
    public _80247Exception400AuthorizationFormat(final String authorization) {
        super(ERR._80247, authorization);
    }
}
