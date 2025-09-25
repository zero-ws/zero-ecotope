package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _401SmsCodeWrongException extends WebException {

    public _401SmsCodeWrongException(final Class<?> clazz,
                                     final String code) {
        super(clazz, code);
    }

    @Override
    public int getCode() {
        return -80229;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNAUTHORIZED;
    }
}
