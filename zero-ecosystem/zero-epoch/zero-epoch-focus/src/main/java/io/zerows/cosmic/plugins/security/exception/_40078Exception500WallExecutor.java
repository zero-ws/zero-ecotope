package io.zerows.cosmic.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.util.Objects;

/**
 * @author lang : 2025-09-30
 */
public class _40078Exception500WallExecutor extends VertxBootException {
    public _40078Exception500WallExecutor(final Class<?> clazz) {
        super(ERR._40078, Objects.requireNonNull(clazz).getName());
    }
}
