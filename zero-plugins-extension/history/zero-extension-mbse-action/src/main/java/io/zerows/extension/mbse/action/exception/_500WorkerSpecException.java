package io.zerows.extension.mbse.action.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500WorkerSpecException extends WebException {

    public _500WorkerSpecException(final Class<?> clazz,
                                   final Class<?> workerCls) {
        super(clazz, workerCls.getName());
    }

    @Override
    public int getCode() {
        return -80404;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
