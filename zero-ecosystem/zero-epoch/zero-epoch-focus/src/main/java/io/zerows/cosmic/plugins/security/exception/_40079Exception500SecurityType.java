package io.zerows.cosmic.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-10-29
 */
public class _40079Exception500SecurityType extends VertxBootException {
    public _40079Exception500SecurityType(final String required,
                                          final String input) {
        super(ERR._40079, required, input);
    }
}
