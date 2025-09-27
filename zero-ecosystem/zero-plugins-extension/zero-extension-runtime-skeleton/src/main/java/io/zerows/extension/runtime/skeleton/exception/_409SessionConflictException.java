package io.zerows.extension.runtime.skeleton.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _409SessionConflictException extends WebException {

    public _409SessionConflictException(final Class<?> clazz,
                                        final String sessionId) {
        super(clazz, sessionId);
    }

    @Override
    public int getCode() {
        return -80214;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}
