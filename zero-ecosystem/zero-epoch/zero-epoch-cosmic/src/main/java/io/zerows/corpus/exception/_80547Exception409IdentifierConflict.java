package io.zerows.corpus.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80547Exception409IdentifierConflict extends VertxWebException {
    public _80547Exception409IdentifierConflict(final String idInput,
                                                final String idConfig) {
        super(ERR._80547, idInput, idConfig);
    }
}
