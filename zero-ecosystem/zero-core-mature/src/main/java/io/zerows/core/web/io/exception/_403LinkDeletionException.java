package io.zerows.core.web.io.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author lang : 2023-05-27
 */
public class _403LinkDeletionException extends WebException {

    public _403LinkDeletionException(final Class<?> clazz,
                                     final String identifier) {
        super(clazz, identifier);
    }

    @Override
    public int getCode() {
        return -80306;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.FORBIDDEN;
    }
}
