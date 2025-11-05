package io.zerows.extension.module.mbseapi.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80406Exception500DefinitionError extends VertxWebException {
    public _80406Exception500DefinitionError(final String key) {
        super(ERR._80406, key);
    }
}
