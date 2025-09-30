package io.zerows.extension.mbse.basement.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80504Exception500NullableAdd extends VertxWebException {
    public _80504Exception500NullableAdd(final String table,
                                         final String column) {
        super(ERR._80504, table, column);
    }
}
