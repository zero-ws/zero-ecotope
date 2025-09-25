package io.zerows.plugins.common.shell.exception;

import io.zerows.core.exception.BootingException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootCommandUnknownException extends BootingException {

    public BootCommandUnknownException(final Class<?> clazz, final String command) {
        super(clazz, command);
    }

    @Override
    public int getCode() {
        return -40073;
    }
}
