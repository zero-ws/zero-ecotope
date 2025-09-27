package io.zerows.core.web.container.exception;

import io.zerows.core.exception.BootingException;

import java.lang.reflect.Method;

public class BootChannelMultiException extends BootingException {

    public BootChannelMultiException(final Class<?> clazz,
                                     final Method method) {
        super(clazz, method.getName(), method.getDeclaringClass().getName());
    }

    @Override
    public int getCode() {
        return -40042;
    }
}
