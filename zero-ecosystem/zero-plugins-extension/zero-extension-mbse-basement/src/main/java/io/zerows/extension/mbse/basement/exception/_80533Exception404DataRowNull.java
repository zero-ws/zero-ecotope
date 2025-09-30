package io.zerows.extension.mbse.basement.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80533Exception404DataRowNull extends VertxWebException {
    public _80533Exception404DataRowNull(final String identifier) {
        super(ERR._80533, identifier);
    }
}
