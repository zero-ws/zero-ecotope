package io.zerows.core.exception.boot;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-26
 */
public class _11011Exception500InvokingPre extends VertxBootException {
    public _11011Exception500InvokingPre(final Method method) {
        super(ERR._11011, method.getName());
    }
}
