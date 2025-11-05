package io.zerows.extension.module.mbseapi.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80403Exception400RequiredParam extends VertxWebException {
    public _80403Exception400RequiredParam(final String fieldName) {
        super(ERR._80403, fieldName);
    }
}
