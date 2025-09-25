package io.zerows.extension.runtime.ambient.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500PrerequisiteSpecException extends WebException {

    public _500PrerequisiteSpecException(final Class<?> clazz,
                                         final String name) {
        super(clazz, name);
    }

    @Override
    public int getCode() {
        return -80303;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
