package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80207Exception401TokenInvalid extends VertxWebException {
    public _80207Exception401TokenInvalid(final String token) {
        super(ERR._80207, token);
    }
}
