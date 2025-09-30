package io.zerows.epoch.container.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60003Exception500EntityCast extends VertxWebException {
    public _60003Exception500EntityCast(final String address, final String message) {
        super(ERR._60003, address, message);
    }
}
