package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

import java.lang.reflect.Method;

public class BootAddressWrongException extends BootingException {

    public BootAddressWrongException(final Class<?> clazz,
                                     final String address,
                                     final Class<?> target,
                                     final Method method) {
        super(clazz, address, target.getName(), method.getName());
    }

    @Override
    public int getCode() {
        return -40012;
    }
}
