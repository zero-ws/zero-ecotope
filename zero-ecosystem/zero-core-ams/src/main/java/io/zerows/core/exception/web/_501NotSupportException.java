package io.zerows.core.exception.web;

import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _501NotSupportException extends WebException {
    public _501NotSupportException(final Class<?> clazz) {
        super(clazz, clazz);
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }

    @Override
    public int getCode() {
        return -60050;
    }

    @Development("IDE视图专用")
    private int __60050() {
        return this.getCode();
    }
}
