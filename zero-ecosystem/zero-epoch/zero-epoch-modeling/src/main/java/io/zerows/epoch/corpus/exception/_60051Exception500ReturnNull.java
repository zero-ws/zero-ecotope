package io.zerows.epoch.corpus.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _60051Exception500ReturnNull extends VertxWebException {
    public _60051Exception500ReturnNull(final Method method) {
        super(ERR._60051, method.getName(), method.getDeclaringClass().getName());
    }
}
