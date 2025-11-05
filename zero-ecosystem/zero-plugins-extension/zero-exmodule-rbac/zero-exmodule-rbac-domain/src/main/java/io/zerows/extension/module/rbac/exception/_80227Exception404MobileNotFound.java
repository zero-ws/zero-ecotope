package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80227Exception404MobileNotFound extends VertxWebException {
    public _80227Exception404MobileNotFound(final String mobile) {
        super(ERR._80227, mobile);
    }
}
