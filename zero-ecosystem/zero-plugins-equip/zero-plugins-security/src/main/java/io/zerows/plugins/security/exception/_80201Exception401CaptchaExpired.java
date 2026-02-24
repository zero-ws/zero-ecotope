package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80201Exception401CaptchaExpired extends VertxWebException {
    public _80201Exception401CaptchaExpired(final String captchaId,
                                            final String captcha) {
        super(ERR._80201, captchaId, captcha);
    }
}
