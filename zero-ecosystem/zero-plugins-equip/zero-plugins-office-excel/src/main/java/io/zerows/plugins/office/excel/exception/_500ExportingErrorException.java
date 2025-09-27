package io.zerows.plugins.office.excel.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _500ExportingErrorException extends WebException {

    public _500ExportingErrorException(final Class<?> clazz,
                                       final String detail) {
        super(clazz, detail);
    }

    @Override
    public int getCode() {
        return -60039;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
}
