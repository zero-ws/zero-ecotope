package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _404AdmitDaoNullException extends WebException {

    public _404AdmitDaoNullException(final Class<?> clazz,
                                     final String daoStr) {
        super(clazz, daoStr);
    }

    @Override
    public int getCode() {
        return -80226;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
