package io.zerows.epoch.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _40049Exception500WorkerConflict extends VertxBootException {
    public _40049Exception500WorkerConflict(final Method method) {
        super(ERR._40049, method.getName(), method.getDeclaringClass());
    }
}
