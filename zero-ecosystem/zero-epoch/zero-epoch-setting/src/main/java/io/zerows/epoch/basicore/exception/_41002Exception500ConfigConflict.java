package io.zerows.epoch.basicore.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-12-15
 */
public class _41002Exception500ConfigConflict extends VertxBootException {
    public _41002Exception500ConfigConflict(final String expected, final String actual) {
        super(ERR._41002, expected, actual);
    }
}
