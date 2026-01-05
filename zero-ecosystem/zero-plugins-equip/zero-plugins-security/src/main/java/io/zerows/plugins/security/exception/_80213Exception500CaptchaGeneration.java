package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80213Exception500CaptchaGeneration extends VertxWebException {
    public _80213Exception500CaptchaGeneration(final String message) {
        super(ERR._80213, message);
    }
}
