package io.zerows.plugins.common.shell.exception;

import io.zerows.core.exception.BootingException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootCommandMissingException extends BootingException {

    public BootCommandMissingException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -40072;
    }
}
