package io.zerows.extension.mbse.basement.exception;

import io.zerows.core.exception.WebException;

public class _400KeyLengthException extends WebException {
    public _400KeyLengthException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80527;
    }
}
