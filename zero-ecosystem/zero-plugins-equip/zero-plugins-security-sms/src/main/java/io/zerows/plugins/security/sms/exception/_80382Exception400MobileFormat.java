package io.zerows.plugins.security.sms.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-12-08
 */
public class _80382Exception400MobileFormat extends VertxWebException {
    public _80382Exception400MobileFormat(final String mobile) {
        super(ERR._80382, mobile);
    }
}
