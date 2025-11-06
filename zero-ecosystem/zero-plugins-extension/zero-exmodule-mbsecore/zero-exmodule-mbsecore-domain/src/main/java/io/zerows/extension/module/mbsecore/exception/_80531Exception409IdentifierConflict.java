package io.zerows.extension.module.mbsecore.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80531Exception409IdentifierConflict extends VertxWebException {
    public _80531Exception409IdentifierConflict(final String identifier) {
        super(ERR._80531, identifier);
    }
}
