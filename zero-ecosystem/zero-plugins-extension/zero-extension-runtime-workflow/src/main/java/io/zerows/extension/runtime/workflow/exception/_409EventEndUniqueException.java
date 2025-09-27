package io.zerows.extension.runtime.workflow.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _409EventEndUniqueException extends WebException {

    public _409EventEndUniqueException(final Class<?> clazz,
                                       final Integer size,
                                       final String definitionId) {
        super(clazz, String.valueOf(size), definitionId);
    }

    @Override
    public int getCode() {
        return -80609;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}