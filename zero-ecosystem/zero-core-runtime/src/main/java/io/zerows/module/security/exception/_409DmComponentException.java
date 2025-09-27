package io.zerows.module.security.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.constant.em.EmSecure;
import io.zerows.core.exception.WebException;

public class _409DmComponentException extends WebException {
    public _409DmComponentException(final Class<?> clazz, final EmSecure.ScDim dim) {
        super(clazz, dim.name());
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }

    @Override
    public int getCode() {
        return -60058;
    }
}
