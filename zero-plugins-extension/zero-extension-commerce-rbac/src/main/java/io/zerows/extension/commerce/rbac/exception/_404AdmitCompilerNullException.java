package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;
import io.zerows.core.constant.em.EmSecure;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _404AdmitCompilerNullException extends WebException {
    public _404AdmitCompilerNullException(final Class<?> clazz, final EmSecure.ScIn in) {
        super(clazz, in.name());
    }

    @Override
    public int getCode() {
        return -80225;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
