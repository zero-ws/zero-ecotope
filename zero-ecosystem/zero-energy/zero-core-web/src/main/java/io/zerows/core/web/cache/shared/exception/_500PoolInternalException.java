package io.zerows.core.web.cache.shared.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500PoolInternalException extends WebException {

    public _500PoolInternalException(final Class<?> clazz,
                                     final String name,
                                     final String method) {
        super(clazz, name, method);
    }

    @Override
    public int getCode() {
        return -60035;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
