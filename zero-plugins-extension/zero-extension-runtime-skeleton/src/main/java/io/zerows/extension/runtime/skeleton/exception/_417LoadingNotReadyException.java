package io.zerows.extension.runtime.skeleton.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author lang : 2023-06-12
 */
public class _417LoadingNotReadyException extends WebException {

    public _417LoadingNotReadyException(final Class<?> target, final String key) {
        super(target, key);
    }

    @Override
    public int getCode() {
        return -80218;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.EXPECTATION_FAILED;
    }
}
