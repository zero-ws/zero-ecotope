package io.zerows.extension.runtime.workflow.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _409InValidInstanceException extends WebException {
    public _409InValidInstanceException(final Class<?> clazz, final String instanceId) {
        super(clazz, instanceId);
    }

    @Override
    public int getCode() {
        return -80605;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_FOUND;
    }
}
