package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80220Exception423UserDisabled extends VertxWebException {
    public _80220Exception423UserDisabled(final String username) {
        super(ERR._80220, username);
    }
}
