package io.zerows.core.web.mbse.exception;

import io.zerows.core.exception.BootingException;

public class BootActSpecificationException extends BootingException {

    public BootActSpecificationException(final Class<?> clazz,
                                         final Boolean isBatch) {
        super(clazz, isBatch);
    }

    @Override
    public int getCode() {
        return -40064;
    }
}
