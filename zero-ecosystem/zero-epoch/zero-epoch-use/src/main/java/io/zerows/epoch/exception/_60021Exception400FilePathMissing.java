package io.zerows.epoch.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60021Exception400FilePathMissing extends VertxWebException {
    public _60021Exception400FilePathMissing(final String filename) {
        super(ERR._60021, filename);
    }
}
