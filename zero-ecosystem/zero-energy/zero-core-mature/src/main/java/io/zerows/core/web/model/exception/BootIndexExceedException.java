package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

public class BootIndexExceedException extends BootingException {

    public BootIndexExceedException(final Class<?> clazz,
                                    final Integer index) {
        super(clazz, String.valueOf(index));
    }

    @Override
    public int getCode() {
        return -40032;
    }
}
