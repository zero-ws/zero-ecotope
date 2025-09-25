package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

import java.lang.reflect.Method;

public class BootUnknownDirectionException extends BootingException {

    public BootUnknownDirectionException(final Class<?> clazz,
                                         final Method method) {
        super(clazz, method);
    }

    @Override
    public int getCode() {
        return -40045;
    }
}
