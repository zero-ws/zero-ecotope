package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500EmptySQLException extends WebException {

    public _500EmptySQLException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80502;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
