package io.zerows.extension.mbse.action.exception;

import io.zerows.core.exception.WebException;

public class _400RequiredParamException extends WebException {

    public _400RequiredParamException(final Class<?> clazz, final String fieldname) {
        super(clazz, fieldname);
    }

    @Override
    public int getCode() {
        return -80403;
    }
}
