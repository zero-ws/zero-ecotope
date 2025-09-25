package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _409UniqueConstraintException extends WebException {

    public _409UniqueConstraintException(final Class<?> clazz,
                                         final Throwable exception) {
        super(clazz, exception.getMessage());
    }

    @Override
    public int getCode() {
        return -80500;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}
