package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80601Exception501EventStartMissing extends VertxWebException {
    public _80601Exception501EventStartMissing(final String definitionId) {
        super(ERR._80601, definitionId);
    }
}
