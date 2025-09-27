package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

import java.lang.reflect.Method;

public class BootIpcMethodTargetException extends BootingException {

    public BootIpcMethodTargetException(final Class<?> clazz,
                                        final Method method,
                                        final String to,
                                        final String name) {
        super(clazz, method, to, name);
    }

    @Override
    public int getCode() {
        return -40043;
    }
}
