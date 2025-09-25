package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _501AoSentenceNullException extends WebException {

    public _501AoSentenceNullException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80507;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }
}
