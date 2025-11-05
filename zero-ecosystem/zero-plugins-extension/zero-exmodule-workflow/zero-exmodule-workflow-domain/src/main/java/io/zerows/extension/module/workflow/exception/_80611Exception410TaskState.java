package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80611Exception410TaskState extends VertxWebException {
    public _80611Exception410TaskState(final String taskId) {
        super(ERR._80611, taskId);
    }
}
