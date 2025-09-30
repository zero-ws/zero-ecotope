package io.zerows.epoch.container.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60052Exception411ContentLength extends VertxWebException {
    public _60052Exception411ContentLength(final int length) {
        super(ERR._60052, length);
    }
}
