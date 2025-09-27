package io.zerows.plugins.integration.git.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _400RepoCommandException extends WebException {

    public _400RepoCommandException(final Class<?> clazz,
                                    final String command,
                                    final String message) {
        super(clazz, command, message);
    }


    @Override
    public int getCode() {
        return -60057;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.BAD_REQUEST;
    }
}
