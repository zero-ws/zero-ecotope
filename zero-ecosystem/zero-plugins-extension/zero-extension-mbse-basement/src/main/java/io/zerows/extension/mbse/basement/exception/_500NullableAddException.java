package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500NullableAddException extends WebException {

    public _500NullableAddException(final Class<?> clazz,
                                    final String table,
                                    final String column) {
        super(clazz, table, column);
    }

    @Override
    public int getCode() {
        return -80504;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
