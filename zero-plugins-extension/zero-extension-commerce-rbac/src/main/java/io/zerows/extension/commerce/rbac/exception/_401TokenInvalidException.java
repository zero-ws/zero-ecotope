package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _401TokenInvalidException extends WebException {

    public _401TokenInvalidException(final Class<?> clazz,
                                     final String token) {
        super(clazz, token);
    }

    @Override
    public int getCode() {
        return -80207;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNAUTHORIZED;
    }
}
