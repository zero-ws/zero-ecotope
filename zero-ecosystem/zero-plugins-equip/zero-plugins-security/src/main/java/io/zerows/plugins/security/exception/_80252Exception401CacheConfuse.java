package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80252Exception401CacheConfuse extends VertxWebException {
    public _80252Exception401CacheConfuse(final String session, final String cache) {
        super(ERR._80252, session, cache);
    }
}
