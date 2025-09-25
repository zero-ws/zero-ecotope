package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _417RelatedSchemaMissingException extends WebException {

    public _417RelatedSchemaMissingException(final Class<?> clazz,
                                             final String unique,
                                             final String entityKey) {
        super(clazz, unique, entityKey);
    }

    @Override
    public int getCode() {
        return -80537;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.EXPECTATION_FAILED;
    }
}
