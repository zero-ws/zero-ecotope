package io.zerows.module.metadata.exception;

import io.zerows.core.exception.BootingException;

public class BootKeyMissingException extends BootingException {

    public BootKeyMissingException(final Class<?> clazz,
                                   final String key) {
        super(clazz, key);
    }

    @Override
    public int getCode() {
        return -40020;
    }
}
