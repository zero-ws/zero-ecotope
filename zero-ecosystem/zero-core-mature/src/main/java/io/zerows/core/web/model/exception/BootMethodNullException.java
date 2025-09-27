package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

public class BootMethodNullException extends BootingException {

    public BootMethodNullException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -40007;
    }
}
