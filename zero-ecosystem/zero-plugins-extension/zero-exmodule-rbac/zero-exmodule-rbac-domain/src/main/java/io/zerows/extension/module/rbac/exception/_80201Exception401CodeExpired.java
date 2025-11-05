package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80201Exception401CodeExpired extends VertxWebException {
    public _80201Exception401CodeExpired(final String clientId,
                                         final String code) {
        super(ERR._80201, clientId, code);
    }
}
