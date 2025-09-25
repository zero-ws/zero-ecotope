package io.zerows.core.web.invocation.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _501RpcRejectException extends WebException {

    public _501RpcRejectException(final Class<?> clazz) {
        super(clazz, clazz);
    }

    @Override
    public int getCode() {
        return -60027;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }
}
