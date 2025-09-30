package io.zerows.extension.commerce.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80206Exception401TokenCounter extends VertxWebException {
    public _80206Exception401TokenCounter(final Integer size,
                                          final String userKey) {
        super(ERR._80206, size, userKey);
    }
}
