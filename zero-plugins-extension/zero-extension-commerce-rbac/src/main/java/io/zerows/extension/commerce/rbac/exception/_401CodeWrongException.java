package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _401CodeWrongException extends WebException {

    public _401CodeWrongException(final Class<?> clazz,
                                  final String code) {
        super(clazz, code);
    }

    @Override
    public int getCode() {
        return -80200;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNAUTHORIZED;
    }
}
