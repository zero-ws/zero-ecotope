package io.zerows.core.exception.web;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60007Exception500InternalServer extends VertxWebException {
    public _60007Exception500InternalServer(final String detail) {
        super(ERR._60007, detail);
    }
}
