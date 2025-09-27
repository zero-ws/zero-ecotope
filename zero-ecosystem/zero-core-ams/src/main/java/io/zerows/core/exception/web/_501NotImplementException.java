package io.zerows.core.exception.web;

import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _501NotImplementException extends WebException {

    public _501NotImplementException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80413;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }

    @Development("IDE视图专用")
    private int __80413() {
        return this.getCode();
    }
}
