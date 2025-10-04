package io.zerows.cosmic.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _40042Exception500ChannelMulti extends VertxBootException {

    public _40042Exception500ChannelMulti(final Method method) {
        super(ERR._40042, method.getName(), method.getDeclaringClass().getName());
    }
}
