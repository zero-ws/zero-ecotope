package io.zerows.core.web.invocation.exception;

import io.zerows.core.exception.BootingException;

import java.lang.reflect.Method;

public class BootWorkerArgumentException extends BootingException {

    public BootWorkerArgumentException(final Class<?> clazz,
                                       final Method method) {
        super(clazz, method.getName(), method.getDeclaringClass().getName());
    }

    @Override
    public int getCode() {
        return -40017;
    }
}
