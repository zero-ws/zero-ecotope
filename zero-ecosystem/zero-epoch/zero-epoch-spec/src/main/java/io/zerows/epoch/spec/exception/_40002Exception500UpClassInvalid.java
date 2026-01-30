package io.zerows.epoch.spec.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40002Exception500UpClassInvalid extends VertxBootException {

    public _40002Exception500UpClassInvalid(final Class<?> clazz) {
        super(ERR._40002, clazz.getName());
    }
}
