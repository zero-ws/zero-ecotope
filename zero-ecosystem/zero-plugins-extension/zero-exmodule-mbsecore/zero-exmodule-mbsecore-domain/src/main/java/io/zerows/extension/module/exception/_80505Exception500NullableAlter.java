package io.zerows.extension.module.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80505Exception500NullableAlter extends VertxWebException {
    public _80505Exception500NullableAlter(final String table,
                                           final String column) {
        super(ERR._80505, table, column);
    }
}
