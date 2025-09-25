package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _501FabricIssueException extends WebException {

    public _501FabricIssueException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80539;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }
}
