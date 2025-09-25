package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _401ImageCodeWrongException extends WebException {

    public _401ImageCodeWrongException(final Class<?> clazz,
                                       final String code) {
        super(clazz, code);
    }

    @Override
    public int getCode() {
        return -80222;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNAUTHORIZED;
    }
}
