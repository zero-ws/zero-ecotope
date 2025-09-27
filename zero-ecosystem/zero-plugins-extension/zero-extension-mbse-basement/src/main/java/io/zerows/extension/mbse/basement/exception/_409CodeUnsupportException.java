package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _409CodeUnsupportException extends WebException {

    public _409CodeUnsupportException(final Class<?> clazz,
                                      final String code) {
        super(clazz, code);
    }

    @Override
    public int getCode() {
        return -80530;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}
