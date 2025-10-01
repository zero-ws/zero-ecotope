package io.zerows.epoch.based.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60004Exception400ParamFromString extends VertxWebException {
    public _60004Exception400ParamFromString(final Class<?> expectedType,
                                             final String literal) {
        super(ERR._60004, literal, expectedType);
    }
}
