package io.zerows.extension.runtime.workflow.exception;

import io.zerows.extension.runtime.workflow.eon.em.PassWay;
import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * Error = 80604
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _409InValidTaskApiException extends WebException {
    public _409InValidTaskApiException(final Class<?> clazz, final PassWay input,
                                       final PassWay expected) {
        super(clazz, input, expected);
    }

    @Override
    public int getCode() {
        return -80610;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.CONFLICT;
    }
}
