package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _417PrimaryKeySizeException extends WebException {

    public _417PrimaryKeySizeException(final Class<?> clazz,
                                       final Integer current,
                                       final String expected) {
        super(clazz, String.valueOf(current), expected);
    }

    @Override
    public int getCode() {
        return -80515;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.EXPECTATION_FAILED;
    }
}
