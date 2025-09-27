package io.zerows.plugins.video.iqiy.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _401QiyTokenException extends WebException {

    public _401QiyTokenException(final Class<?> clazz,
                                 final String clientId) {
        super(clazz, clientId);
    }

    @Override
    public int getCode() {
        return -20002;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNAUTHORIZED;
    }
}
