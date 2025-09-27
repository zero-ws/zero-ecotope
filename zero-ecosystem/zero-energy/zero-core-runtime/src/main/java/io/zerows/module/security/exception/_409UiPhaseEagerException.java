package io.zerows.module.security.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.constant.em.EmSecure;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _409UiPhaseEagerException extends WebException {

    public _409UiPhaseEagerException(final Class<?> clazz, final EmSecure.ActPhase phase) {
        super(clazz, phase.name());
    }

    @Override
    public int getCode() {
        return -80224;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}
