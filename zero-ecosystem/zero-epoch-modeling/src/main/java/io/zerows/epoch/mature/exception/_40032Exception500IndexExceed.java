package io.zerows.epoch.mature.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40032Exception500IndexExceed extends VertxBootException {
    public _40032Exception500IndexExceed(final int index) {
        super(ERR._40032, index);
    }
}
