package io.zerows.extension.runtime.crud.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _409ModuleConflictException extends WebException {

    public _409ModuleConflictException(final Class<?> clazz,
                                       final String tableName, final String requestTable) {
        super(clazz, tableName, requestTable);
    }

    @Override
    public int getCode() {
        return -80103;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}
