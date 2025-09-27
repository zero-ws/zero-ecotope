package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _404ActionMissingException extends WebException {

    public _404ActionMissingException(final Class<?> clazz,
                                      final String request) {
        super(clazz, request);
    }

    @Override
    public int getCode() {
        return -80209;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
