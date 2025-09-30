package io.zerows.extension.runtime.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80603Exception404WorkflowNull extends VertxWebException {
    public _80603Exception404WorkflowNull(final String definitionKey) {
        super(ERR._80603, definitionKey);
    }
}
