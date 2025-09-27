package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _417DataUnexpectException extends WebException {

    public _417DataUnexpectException(final Class<?> clazz,
                                     final String table,
                                     final String rows) {
        super(clazz, table, rows);
    }

    @Override
    public int getCode() {
        return -80519;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.EXPECTATION_FAILED;
    }
}
