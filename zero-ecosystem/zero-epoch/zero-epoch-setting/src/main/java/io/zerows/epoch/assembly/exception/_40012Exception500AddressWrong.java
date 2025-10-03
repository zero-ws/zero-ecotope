package io.zerows.epoch.assembly.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _40012Exception500AddressWrong extends VertxBootException {
    public _40012Exception500AddressWrong(final String address,
                                          final Class<?> target,
                                          final Method method) {
        super(ERR._40012, address, target.getName(), method.getName());
    }
}
