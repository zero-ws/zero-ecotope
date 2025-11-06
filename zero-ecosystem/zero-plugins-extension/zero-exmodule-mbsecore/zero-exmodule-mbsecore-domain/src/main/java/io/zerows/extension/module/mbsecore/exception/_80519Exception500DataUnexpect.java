package io.zerows.extension.module.mbsecore.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80519Exception500DataUnexpect extends VertxWebException {
    public _80519Exception500DataUnexpect(final String table, final String rows) {
        super(ERR._80519, table, rows);
    }
}
