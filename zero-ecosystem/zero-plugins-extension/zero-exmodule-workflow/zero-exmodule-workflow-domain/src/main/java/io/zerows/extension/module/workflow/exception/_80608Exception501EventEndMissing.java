package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80608Exception501EventEndMissing extends VertxWebException {
    public _80608Exception501EventEndMissing(final String definitionId) {
        super(ERR._80608, definitionId);
    }
}
