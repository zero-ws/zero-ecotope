package io.zerows.extension.runtime.workflow.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _404WorkflowNullException extends WebException {

    public _404WorkflowNullException(final Class<?> clazz,
                                     final String definitionKey) {
        super(clazz, definitionKey);
    }

    @Override
    public int getCode() {
        return -80603;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
