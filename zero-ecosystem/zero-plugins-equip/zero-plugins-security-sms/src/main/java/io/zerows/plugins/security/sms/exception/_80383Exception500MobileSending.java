package io.zerows.plugins.security.sms.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-12-08
 */
public class _80383Exception500MobileSending extends VertxWebException {
    public _80383Exception500MobileSending(final String mobile) {
        super(ERR._80383, mobile);
    }
}
