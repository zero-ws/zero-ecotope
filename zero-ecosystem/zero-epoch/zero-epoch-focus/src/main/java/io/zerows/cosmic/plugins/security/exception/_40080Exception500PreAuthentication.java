package io.zerows.cosmic.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-10-29
 */
public class _40080Exception500PreAuthentication extends VertxBootException {
    public _40080Exception500PreAuthentication(final int size,
                                               final String path) {
        super(ERR._40080, size, path);
    }
}
