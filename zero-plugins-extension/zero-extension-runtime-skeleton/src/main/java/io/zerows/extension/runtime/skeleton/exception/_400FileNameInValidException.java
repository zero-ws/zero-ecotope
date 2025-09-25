package io.zerows.extension.runtime.skeleton.exception;

import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _400FileNameInValidException extends WebException {

    public _400FileNameInValidException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -81002;
    }
}
