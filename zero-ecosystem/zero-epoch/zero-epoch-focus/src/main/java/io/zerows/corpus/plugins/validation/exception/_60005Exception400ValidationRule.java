package io.zerows.corpus.plugins.validation.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60005Exception400ValidationRule extends VertxWebException {
    public _60005Exception400ValidationRule(final String field,
                                            final Object value,
                                            final String message) {
        super(ERR._60005, field, value, message);
    }
}
