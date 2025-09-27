package io.zerows.core.web.session.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500SessionClientInitException extends WebException {

    public _500SessionClientInitException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -20005;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
