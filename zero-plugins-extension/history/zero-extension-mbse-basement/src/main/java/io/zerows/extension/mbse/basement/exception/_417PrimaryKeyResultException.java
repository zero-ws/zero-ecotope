package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _417PrimaryKeyResultException extends WebException {

    public _417PrimaryKeyResultException(final Class<?> clazz,
                                         final String name,
                                         final String key) {
        super(clazz, name, key);
    }

    @Override
    public int getCode() {
        return -80514;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.EXPECTATION_FAILED;
    }
}
