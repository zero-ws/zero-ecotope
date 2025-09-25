package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _417PrimaryKeySpecException extends WebException {

    public _417PrimaryKeySpecException(final Class<?> clazz,
                                       final String identifier) {
        super(clazz, identifier);
    }

    @Override
    public int getCode() {
        return -80516;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.EXPECTATION_FAILED;
    }
}
