package io.zerows.extension.commerce.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80208Exception401TokenExpired extends VertxWebException {
    public _80208Exception401TokenExpired(final String token) {
        super(ERR._80208, token);
    }
}
