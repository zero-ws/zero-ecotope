package io.zerows.epoch.corpus.container.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _40013Exception500ReturnType extends VertxBootException {
    public _40013Exception500ReturnType(final Method method) {
        super(ERR._40013, method.getName(), method.getDeclaringClass().getName());
    }
}
