package io.zerows.core.exception.web;

import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500InternalServerException extends WebException {

    public _500InternalServerException(final Class<?> clazz,
                                       final String details) {
        super(clazz, details);
    }

    @Override
    public int getCode() {
        return -60007;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }

    @Development("IDE视图专用")
    private int __60007() {
        return this.getCode();
    }
}
