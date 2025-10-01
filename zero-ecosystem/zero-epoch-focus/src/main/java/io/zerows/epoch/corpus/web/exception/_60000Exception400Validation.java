package io.zerows.epoch.corpus.web.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _60000Exception400Validation extends VertxWebException {

    public _60000Exception400Validation(final Class<?> target,
                                        final Method method,
                                        final String info) {
        super(ERR._60000, target.getName(), method.getName(), info);
    }
}
