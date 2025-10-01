package io.zerows.epoch.corpus.web.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40077Exception400WallProviderConflict extends VertxBootException {
    public _40077Exception400WallProviderConflict(final Class<?> provider) {
        super(ERR._40077, provider.getName());
    }
}
