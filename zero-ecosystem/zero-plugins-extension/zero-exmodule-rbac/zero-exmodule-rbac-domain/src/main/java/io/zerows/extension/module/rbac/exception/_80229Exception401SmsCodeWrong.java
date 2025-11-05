package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80229Exception401SmsCodeWrong extends VertxWebException {
    public _80229Exception401SmsCodeWrong(final String smsCode) {
        super(ERR._80229, smsCode);
    }
}
