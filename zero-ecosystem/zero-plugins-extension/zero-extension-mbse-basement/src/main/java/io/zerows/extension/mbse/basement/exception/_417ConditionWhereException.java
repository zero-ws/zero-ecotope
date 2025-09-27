package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _417ConditionWhereException extends WebException {

    public _417ConditionWhereException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80523;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.EXPECTATION_FAILED;
    }
}
