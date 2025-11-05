package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80607Exception404RunOnSupplier extends VertxWebException {
    public _80607Exception404RunOnSupplier(final String eventType) {
        super(ERR._80607, eventType);
    }
}
