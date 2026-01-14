package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80242Exception400CaptchaRequired extends VertxWebException {
    public _80242Exception400CaptchaRequired(final String fieldOrCaptcha) {
        super(ERR._80242, fieldOrCaptcha);
    }
}
