package io.zerows.extension.runtime.ambient.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500ApplicationInitException extends WebException {

    public _500ApplicationInitException(final Class<?> clazz,
                                        final String name) {
        super(clazz, name);
    }

    @Override
    public int getCode() {
        return -80301;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
