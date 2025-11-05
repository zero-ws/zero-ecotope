package io.zerows.extension.module.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80518Exception500DataTransaction extends VertxWebException {
    public _80518Exception500DataTransaction(final Throwable ex) {
        super(ERR._80518, ex.getMessage());
    }
}
