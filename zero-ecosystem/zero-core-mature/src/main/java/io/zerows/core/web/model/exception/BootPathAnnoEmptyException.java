package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

public class BootPathAnnoEmptyException extends BootingException {

    public BootPathAnnoEmptyException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -40006;
    }
}
