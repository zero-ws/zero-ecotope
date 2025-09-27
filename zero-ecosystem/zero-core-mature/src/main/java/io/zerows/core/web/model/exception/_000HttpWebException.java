package io.zerows.core.web.model.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;
import io.vertx.ext.web.handler.HttpException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _000HttpWebException extends WebException {
    private final transient HttpException ex;

    public _000HttpWebException(final Class<?> clazz, final HttpException ex) {
        super(clazz, ex.getStatusCode(), ex.getMessage());
        this.ex = ex;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.fromCode(this.ex.getStatusCode());
    }

    @Override
    public int getCode() {
        return -60049;
    }
}
