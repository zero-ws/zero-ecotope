package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _401PasswordWrongException extends WebException {

    public _401PasswordWrongException(final Class<?> clazz,
                                      final String username) {
        super(clazz, username);
    }

    @Override
    public int getCode() {
        return -80204;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNAUTHORIZED;
    }
}
