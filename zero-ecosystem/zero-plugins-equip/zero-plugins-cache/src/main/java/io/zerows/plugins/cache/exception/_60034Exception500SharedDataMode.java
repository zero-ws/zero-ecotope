package io.zerows.plugins.cache.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60034Exception500SharedDataMode extends VertxWebException {

    public _60034Exception500SharedDataMode(final Throwable ex) {
        super(ERR._60034, ex.getMessage());
    }
}
