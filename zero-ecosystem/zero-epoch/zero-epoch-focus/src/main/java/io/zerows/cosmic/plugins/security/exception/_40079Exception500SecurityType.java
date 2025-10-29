package io.zerows.cosmic.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.platform.enums.SecurityType;

/**
 * @author lang : 2025-10-29
 */
public class _40079Exception500SecurityType extends VertxBootException {
    public _40079Exception500SecurityType(final SecurityType required,
                                          final SecurityType input) {
        super(ERR._40079, required, input);
    }
}
