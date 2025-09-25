package io.zerows.extension.mbse.action.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500ConsumerSpecException extends WebException {
    public _500ConsumerSpecException(final Class<?> clazz, final Class<?> target) {
        super(clazz, target.getName());
    }

    @Override
    public int getCode() {
        return -80405;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
