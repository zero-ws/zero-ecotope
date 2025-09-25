package io.zerows.core.web.validation.exception;

import io.zerows.core.exception.WebException;

public class _400ValidationRuleException extends WebException {

    public _400ValidationRuleException(final Class<?> clazz,
                                       final String field,
                                       final Object value,
                                       final String message) {
        super(clazz, field, value, message);
    }

    @Override
    public int getCode() {
        return -60005;
    }
}
