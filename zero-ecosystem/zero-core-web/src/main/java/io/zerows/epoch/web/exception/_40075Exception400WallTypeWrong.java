package io.zerows.epoch.web.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40075Exception400WallTypeWrong extends VertxBootException {

    public _40075Exception400WallTypeWrong(final String key, final Class<?> target) {
        super(ERR._40075, key, target.getName());
    }
}
