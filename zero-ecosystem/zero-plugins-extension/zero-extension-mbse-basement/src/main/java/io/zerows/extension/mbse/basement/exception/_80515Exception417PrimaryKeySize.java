package io.zerows.extension.mbse.basement.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80515Exception417PrimaryKeySize extends VertxWebException {
    public _80515Exception417PrimaryKeySize(final Integer current,
                                            final String expected) {
        super(ERR._80515, current, expected);
    }
}
