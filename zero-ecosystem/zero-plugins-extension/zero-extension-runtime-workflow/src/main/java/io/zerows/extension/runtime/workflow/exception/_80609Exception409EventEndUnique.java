package io.zerows.extension.runtime.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80609Exception409EventEndUnique extends VertxWebException {
    public _80609Exception409EventEndUnique(final Integer size,
                                            final String definitionId) {
        super(ERR._80609, size, definitionId);
    }
}
