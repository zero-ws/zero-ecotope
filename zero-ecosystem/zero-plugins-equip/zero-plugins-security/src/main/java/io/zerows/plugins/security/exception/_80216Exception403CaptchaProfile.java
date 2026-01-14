package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80216Exception403CaptchaProfile extends VertxWebException {
    public _80216Exception403CaptchaProfile(final String userId) {
        super(ERR._80216, userId);
    }
}
