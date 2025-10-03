package io.zerows.epoch.assembly.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40052Exception500FilterSpecification extends VertxBootException {
    public _40052Exception500FilterSpecification(final Class<?> filterCls) {
        super(ERR._40052, filterCls.getName());
    }
}
