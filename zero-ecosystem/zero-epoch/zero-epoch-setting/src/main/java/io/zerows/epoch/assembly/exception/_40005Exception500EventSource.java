package io.zerows.epoch.assembly.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40005Exception500EventSource extends VertxBootException {

    public _40005Exception500EventSource(final Class<?> endpointCls) {
        super(ERR._40005, endpointCls.getName());
    }
}
