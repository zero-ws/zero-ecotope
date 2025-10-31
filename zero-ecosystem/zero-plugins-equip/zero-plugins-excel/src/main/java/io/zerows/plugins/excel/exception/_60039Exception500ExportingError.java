package io.zerows.plugins.excel.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60039Exception500ExportingError extends VertxWebException {

    public _60039Exception500ExportingError(final String detail) {
        super(ERR._60039, detail);
    }
}
