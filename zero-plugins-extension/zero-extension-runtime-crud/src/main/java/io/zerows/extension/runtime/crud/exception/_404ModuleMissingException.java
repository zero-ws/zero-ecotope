package io.zerows.extension.runtime.crud.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _404ModuleMissingException extends WebException {

    public _404ModuleMissingException(final Class<?> clazz,
                                      final String module) {
        super(clazz, module);
    }

    @Override
    public int getCode() {
        return -80100;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
