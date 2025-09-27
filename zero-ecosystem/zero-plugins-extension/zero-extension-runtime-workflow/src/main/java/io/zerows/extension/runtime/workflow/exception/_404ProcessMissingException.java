package io.zerows.extension.runtime.workflow.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _404ProcessMissingException extends WebException {

    public _404ProcessMissingException(final Class<?> clazz, final String code) {
        super(clazz, code);
    }

    @Override
    public int getCode() {
        return -80600;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
