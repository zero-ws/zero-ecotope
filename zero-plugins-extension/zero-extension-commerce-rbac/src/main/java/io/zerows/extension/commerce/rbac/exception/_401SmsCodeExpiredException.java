package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _401SmsCodeExpiredException extends WebException {

    public _401SmsCodeExpiredException(final Class<?> clazz,
                                       final String clientId,
                                       final String code) {
        super(clazz, clientId, code);
    }

    @Override
    public int getCode() {
        return -80230;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNAUTHORIZED;
    }
}
