package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80600Exception404ProcessMissing extends VertxWebException {
    public _80600Exception404ProcessMissing(final String code) {
        super(ERR._80600, code);
    }
}
