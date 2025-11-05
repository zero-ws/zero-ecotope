package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80210Exception404ResourceMissing extends VertxWebException {
    public _80210Exception404ResourceMissing(final String resourceId,
                                             final String request) {
        super(ERR._80210, resourceId, request);
    }
}
