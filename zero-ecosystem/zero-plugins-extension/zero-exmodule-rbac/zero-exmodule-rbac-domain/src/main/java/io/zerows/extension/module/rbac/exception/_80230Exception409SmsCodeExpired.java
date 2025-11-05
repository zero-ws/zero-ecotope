package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80230Exception409SmsCodeExpired extends VertxWebException {
    public _80230Exception409SmsCodeExpired(final String clientId,
                                            final String code) {
        super(ERR._80230, clientId, code);
    }
}
