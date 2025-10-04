package io.zerows.cosmic.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40040Exception400WallKeyMissing extends VertxBootException {

    public _40040Exception400WallKeyMissing(final String key, final Class<?> target) {
        super(ERR._40040, key, target.getName());
    }
}
