package io.zerows.extension.runtime.workflow.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * Error = 80604
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _409InValidStartException extends WebException {
    public _409InValidStartException(final Class<?> clazz, final String definitionKey) {
        super(clazz, definitionKey);
    }

    @Override
    public int getCode() {
        return -80604;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}
