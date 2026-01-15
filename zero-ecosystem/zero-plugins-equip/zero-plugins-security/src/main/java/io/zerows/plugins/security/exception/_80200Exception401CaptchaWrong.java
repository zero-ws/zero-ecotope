package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80200Exception401CaptchaWrong extends VertxWebException {
    public _80200Exception401CaptchaWrong(final String captcha) {
        super(ERR._80200, captcha);
    }
}
