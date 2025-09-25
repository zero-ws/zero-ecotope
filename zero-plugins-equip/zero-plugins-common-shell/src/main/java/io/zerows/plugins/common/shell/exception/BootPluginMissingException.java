package io.zerows.plugins.common.shell.exception;

import io.zerows.core.exception.BootingException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootPluginMissingException extends BootingException {

    public BootPluginMissingException(final Class<?> clazz,
                                      final String name) {
        super(clazz, name);
    }

    @Override
    public int getCode() {
        return -40074;
    }
}
