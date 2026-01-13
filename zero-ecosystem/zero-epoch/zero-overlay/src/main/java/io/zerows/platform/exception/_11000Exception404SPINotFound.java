package io.zerows.platform.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-26
 */
public class _11000Exception404SPINotFound extends VertxBootException {
    public _11000Exception404SPINotFound(final Class<?> clazz) {
        super(ERR._11000, clazz.getName());
    }
}
