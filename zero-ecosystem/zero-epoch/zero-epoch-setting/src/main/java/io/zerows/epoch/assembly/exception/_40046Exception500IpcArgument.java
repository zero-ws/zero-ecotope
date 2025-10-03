package io.zerows.epoch.assembly.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _40046Exception500IpcArgument extends VertxBootException {
    public _40046Exception500IpcArgument(final Method method) {
        super(ERR._40046, method);
    }
}
