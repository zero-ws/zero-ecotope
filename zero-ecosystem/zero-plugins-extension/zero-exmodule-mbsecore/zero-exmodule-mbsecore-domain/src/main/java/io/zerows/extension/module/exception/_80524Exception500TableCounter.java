package io.zerows.extension.module.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80524Exception500TableCounter extends VertxWebException {
    public _80524Exception500TableCounter(final String size) {
        super(ERR._80524, size);
    }
}
