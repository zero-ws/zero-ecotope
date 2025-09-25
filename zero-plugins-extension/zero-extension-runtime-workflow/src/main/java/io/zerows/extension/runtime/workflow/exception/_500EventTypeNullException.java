package io.zerows.extension.runtime.workflow.exception;

import io.zerows.core.exception.WebException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class _500EventTypeNullException extends WebException {
    public _500EventTypeNullException(final Class<?> clazz, final String taskKey) {
        super(clazz, taskKey);
    }

    @Override
    public int getCode() {
        return -80606;
    }
}
