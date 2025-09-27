package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _400FileRequiredException extends WebException {

    public _400FileRequiredException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80526;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.BAD_REQUEST;
    }
}
