package io.zerows.extension.mbse.basement.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80509Exception500TypeAlter extends VertxWebException {
    public _80509Exception500TypeAlter(final String table,
                                       final String column) {
        super(ERR._80509, table, column);
    }
}
