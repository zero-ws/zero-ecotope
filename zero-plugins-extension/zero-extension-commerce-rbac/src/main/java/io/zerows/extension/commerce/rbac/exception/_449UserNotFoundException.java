package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _449UserNotFoundException extends WebException {

    public _449UserNotFoundException(final Class<?> clazz,
                                     final String username) {
        super(clazz, username);
    }

    @Override
    public int getCode() {
        return -80203;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.RETRY_WITH;
    }
}
