package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
@Deprecated
public class _80222Exception401ImageCodeWrong extends VertxWebException {
    public _80222Exception401ImageCodeWrong(final String imageCode) {
        super(ERR._80222, imageCode);
    }
}
