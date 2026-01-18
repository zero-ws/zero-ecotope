package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80250Exception401SessionNull extends VertxWebException {
    public _80250Exception401SessionNull(final String dataJ) {
        super(ERR._80250, dataJ);
    }
}
