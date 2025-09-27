package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _417ConditionEmptyException extends WebException {

    public _417ConditionEmptyException(final Class<?> clazz,
                                       final String table) {
        super(clazz, table);
    }

    @Override
    public int getCode() {
        return -80522;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.EXPECTATION_FAILED;
    }
}
