package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80606Exception500EventTypeNull extends VertxWebException {
    public _80606Exception500EventTypeNull(final String taskKey) {
        super(ERR._80606, taskKey);
    }
}
