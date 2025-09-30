package io.zerows.module.metadata.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _15002Exception500JexlExpression extends VertxWebException {

    public _15002Exception500JexlExpression(final String expression,
                                            final Throwable ex) {
        super(ERR._15002, expression, ex.getMessage());
    }
}
