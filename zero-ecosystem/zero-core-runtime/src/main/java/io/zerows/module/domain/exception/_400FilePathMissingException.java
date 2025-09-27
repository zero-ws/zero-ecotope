package io.zerows.module.domain.exception;

import io.zerows.core.exception.WebException;

public class _400FilePathMissingException extends WebException {

    public _400FilePathMissingException(final Class<?> clazz,
                                        final String filename) {
        super(clazz, filename);
    }

    @Override
    public int getCode() {
        return -60021;
    }
}
