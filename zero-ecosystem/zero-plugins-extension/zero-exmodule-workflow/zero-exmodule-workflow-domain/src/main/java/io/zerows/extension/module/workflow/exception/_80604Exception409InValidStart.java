package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80604Exception409InValidStart extends VertxWebException {
    public _80604Exception409InValidStart(final String definitionKey) {
        super(ERR._80604, definitionKey);
    }
}
