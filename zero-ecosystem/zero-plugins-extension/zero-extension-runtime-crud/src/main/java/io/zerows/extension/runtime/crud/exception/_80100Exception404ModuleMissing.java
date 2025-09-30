package io.zerows.extension.runtime.crud.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80100Exception404ModuleMissing extends VertxWebException {

    public _80100Exception404ModuleMissing(final String module) {
        super(ERR._80100, module);
    }
}
