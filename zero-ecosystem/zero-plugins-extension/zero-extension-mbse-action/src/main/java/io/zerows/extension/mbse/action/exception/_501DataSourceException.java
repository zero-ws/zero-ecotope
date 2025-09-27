package io.zerows.extension.mbse.action.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _501DataSourceException extends WebException {

    public _501DataSourceException(final Class<?> clazz,
                                   final String headers) {
        super(clazz, headers);
    }

    @Override
    public int getCode() {
        return -80412;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }
}
