package io.zerows.epoch.corpus.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _40043Exception500IpcTarget extends VertxBootException {
    public _40043Exception500IpcTarget(final Method method,
                                       final String to,
                                       final String name) {
        super(ERR._40043, method, to, name);
    }
}
