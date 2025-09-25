package io.zerows.plugins.common.shell.exception;

import io.zerows.core.exception.BootingException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootInternalConflictException extends BootingException {

    public BootInternalConflictException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -40070;
    }
}
