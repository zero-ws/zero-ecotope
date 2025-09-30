package io.zerows.extension.commerce.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80203Exception404UserNotFound extends VertxWebException {
    public _80203Exception404UserNotFound(final String username) {
        super(ERR._80203, username);
    }
}
