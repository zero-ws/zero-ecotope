package io.zerows.core.web.container.exception;

import io.zerows.core.exception.BootingException;

public class BootUpClassInvalidException extends BootingException {

    public BootUpClassInvalidException(final Class<?> clazz,
                                       final String className) {
        super(clazz, className);
    }

    @Override
    public int getCode() {
        return -40002;
    }
}
