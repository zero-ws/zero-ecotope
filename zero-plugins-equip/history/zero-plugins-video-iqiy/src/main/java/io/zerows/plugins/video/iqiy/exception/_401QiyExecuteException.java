package io.zerows.plugins.video.iqiy.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _401QiyExecuteException extends WebException {

    public _401QiyExecuteException(final Class<?> clazz,
                                   final String code,
                                   final String message) {
        super(clazz, code, message);
    }

    @Override
    public int getCode() {
        return -20001;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNAUTHORIZED;
    }
}
