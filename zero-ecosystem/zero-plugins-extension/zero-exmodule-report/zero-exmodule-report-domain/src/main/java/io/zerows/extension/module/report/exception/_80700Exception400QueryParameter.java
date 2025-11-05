package io.zerows.extension.module.report.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80700Exception400QueryParameter extends VertxWebException {
    public _80700Exception400QueryParameter(final String queryJ) {
        super(ERR._80700, queryJ);
    }
}
