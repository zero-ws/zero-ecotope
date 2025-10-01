package io.zerows.epoch.corpus.mature.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _40017Exception500WorkerArgument extends VertxBootException {

    public _40017Exception500WorkerArgument(final Method method) {
        super(ERR._40017, method.getName(), method.getDeclaringClass().getName());
    }
}
