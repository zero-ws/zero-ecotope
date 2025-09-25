package io.zerows.extension.mbse.basement.exception;

import io.zerows.core.exception.WebException;

public class _400ExcelTplErrorException extends WebException {

    public _400ExcelTplErrorException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80512;
    }
}
