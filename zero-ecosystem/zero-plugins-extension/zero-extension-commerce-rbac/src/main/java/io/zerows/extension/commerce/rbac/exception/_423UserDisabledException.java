package io.zerows.extension.commerce.rbac.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _423UserDisabledException extends WebException {

    public _423UserDisabledException(final Class<?> clazz, final String username) {
        super(clazz, username);
    }

    @Override
    public int getCode() {
        return -80220;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.LOCKED;
    }
}
