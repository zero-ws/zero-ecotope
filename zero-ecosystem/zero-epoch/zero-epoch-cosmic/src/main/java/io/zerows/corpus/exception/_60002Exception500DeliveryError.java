package io.zerows.corpus.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60002Exception500DeliveryError extends VertxWebException {
    public _60002Exception500DeliveryError(final String address,
                                           final String message) {
        super(ERR._60002, address, message);
    }
}
