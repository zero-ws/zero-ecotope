package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80209Exception404ActionMissing extends VertxWebException {
    public _80209Exception404ActionMissing(final String action) {
        super(ERR._80209, action);
    }
}
