package io.zerows.core.database.jooq.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _501JooqReferenceException extends WebException {

    public _501JooqReferenceException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80217;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }
}
