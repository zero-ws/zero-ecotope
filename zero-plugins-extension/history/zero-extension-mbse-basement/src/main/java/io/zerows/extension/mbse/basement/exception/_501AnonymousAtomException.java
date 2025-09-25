package io.zerows.extension.mbse.basement.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _501AnonymousAtomException extends WebException {

    public _501AnonymousAtomException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -80540;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }
}
