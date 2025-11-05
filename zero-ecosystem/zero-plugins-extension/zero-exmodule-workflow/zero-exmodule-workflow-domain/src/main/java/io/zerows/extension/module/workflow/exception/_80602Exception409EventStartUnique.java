package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80602Exception409EventStartUnique extends VertxWebException {
    public _80602Exception409EventStartUnique(final Integer size,
                                              final String definitionId) {
        super(ERR._80602, size, definitionId);
    }
}
