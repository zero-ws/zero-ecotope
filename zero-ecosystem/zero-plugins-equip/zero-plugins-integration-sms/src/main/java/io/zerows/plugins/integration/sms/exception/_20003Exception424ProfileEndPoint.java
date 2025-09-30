package io.zerows.plugins.integration.sms.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

import java.util.Objects;

/**
 * @author lang : 2025-09-30
 */
public class _20003Exception424ProfileEndPoint extends VertxWebException {
    public _20003Exception424ProfileEndPoint(final Throwable ex) {
        super(ERR._20003, Objects.requireNonNull(ex).getMessage());
    }
}
