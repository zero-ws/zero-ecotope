package io.zerows.extension.skeleton.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80219Exception403TokenGeneration extends VertxWebException {
    public _80219Exception403TokenGeneration(final int size) {
        super(ERR._80219, size);
    }
}
