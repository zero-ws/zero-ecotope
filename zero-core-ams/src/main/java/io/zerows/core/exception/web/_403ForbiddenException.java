package io.zerows.core.exception.web;

import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _403ForbiddenException extends WebException {

    public _403ForbiddenException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -60013;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.FORBIDDEN;
    }

    @Development("IDE视图专用")
    private int __60013() {
        return this.getCode();
    }
}
