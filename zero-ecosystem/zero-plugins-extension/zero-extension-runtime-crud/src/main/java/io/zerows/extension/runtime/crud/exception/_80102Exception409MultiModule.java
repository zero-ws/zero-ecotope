package io.zerows.extension.runtime.crud.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80102Exception409MultiModule extends VertxWebException {

    public _80102Exception409MultiModule(final int size) {
        super(ERR._80102, size);
    }
}
