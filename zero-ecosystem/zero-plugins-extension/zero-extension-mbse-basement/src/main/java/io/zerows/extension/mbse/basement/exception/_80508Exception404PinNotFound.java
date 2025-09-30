package io.zerows.extension.mbse.basement.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80508Exception404PinNotFound extends VertxWebException {
    public _80508Exception404PinNotFound(final String pin) {
        super(ERR._80508, pin);
    }
}
