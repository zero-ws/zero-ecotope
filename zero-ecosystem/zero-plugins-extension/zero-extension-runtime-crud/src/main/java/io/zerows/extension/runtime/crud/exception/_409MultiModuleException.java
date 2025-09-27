package io.zerows.extension.runtime.crud.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _409MultiModuleException extends WebException {

    public _409MultiModuleException(final Class<?> clazz,
                                    final int size) {
        super(clazz, String.valueOf(size));
    }

    @Override
    public int getCode() {
        return -80102;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}
