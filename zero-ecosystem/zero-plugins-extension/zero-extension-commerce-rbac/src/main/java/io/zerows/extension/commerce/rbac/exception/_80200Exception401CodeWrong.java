package io.zerows.extension.commerce.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80200Exception401CodeWrong extends VertxWebException {
    public _80200Exception401CodeWrong(final String code) {
        super(ERR._80200, code);
    }
}
