package io.zerows.extension.crud.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80103Exception409ModuleConflict extends VertxWebException {

    public _80103Exception409ModuleConflict(final String tableName, final String requestTable) {
        super(ERR._80103, tableName, requestTable);
    }
}
