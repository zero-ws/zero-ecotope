package io.zerows.epoch.mature.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80306Exception403LinkDeletion extends VertxWebException {
    public _80306Exception403LinkDeletion(final String identifier) {
        super(ERR._80306, identifier);
    }
}
