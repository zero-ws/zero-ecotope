package io.zerows.cosmic.plugins.validation.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _60000Exception400Validation extends VertxWebException {

    private final String messageDisplay;

    public _60000Exception400Validation(final Class<?> target,
                                        final Method method,
                                        final String info) {
        super(ERR._60000, target.getName(), method.getName(), info);
        this.messageDisplay = info;
    }

    @Override
    public String getInfo() {
        return this.messageDisplay;
    }
}
