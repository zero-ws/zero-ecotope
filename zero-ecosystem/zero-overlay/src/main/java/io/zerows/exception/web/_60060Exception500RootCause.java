package io.zerows.exception.web;

import io.r2mo.vertx.common.exception.VertxWebException;

import java.util.Objects;

/**
 * @author lang : 2025-09-30
 */
public class _60060Exception500RootCause extends VertxWebException {
    public _60060Exception500RootCause(final Throwable cause) {
        super(ERR._60060, Objects.requireNonNull(cause).getMessage());
    }
}
