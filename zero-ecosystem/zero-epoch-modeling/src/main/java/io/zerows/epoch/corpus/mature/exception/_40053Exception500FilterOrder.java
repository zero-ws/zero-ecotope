package io.zerows.epoch.corpus.mature.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40053Exception500FilterOrder extends VertxBootException {
    public _40053Exception500FilterOrder(final Class<?> filterCls) {
        super(ERR._40053, filterCls.getName());
    }
}
