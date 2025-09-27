package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500DwarfInstanceNullException extends WebException {

    public _500DwarfInstanceNullException(final Class<?> clazz,
                                          final String type) {
        super(clazz, type);
    }

    @Override
    public int getCode() {
        return -80215;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
