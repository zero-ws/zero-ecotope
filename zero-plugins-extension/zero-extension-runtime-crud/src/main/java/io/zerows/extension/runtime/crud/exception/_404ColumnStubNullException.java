package io.zerows.extension.runtime.crud.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _404ColumnStubNullException extends WebException {

    public _404ColumnStubNullException(final Class<?> clazz,
                                       final String name) {
        super(clazz, name);
    }

    @Override
    public int getCode() {
        return -80101;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
