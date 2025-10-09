package io.zerows.platform.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60026Exception400QrOpUnknown extends VertxWebException {
    public _60026Exception400QrOpUnknown(final String op) {
        super(ERR._60026, op);
    }
}
