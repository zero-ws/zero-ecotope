package io.zerows.plugins.excel.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60037Exception404ExcelFileNull extends VertxWebException {
    public _60037Exception404ExcelFileNull(final String filename) {
        super(ERR._60037, filename);
    }
}
