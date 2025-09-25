package io.zerows.plugins.common.shell.exception;

import io.zerows.core.exception.BootingException;
import org.apache.commons.cli.ParseException;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootCommandParseException extends BootingException {

    public BootCommandParseException(final Class<?> clazz, final String input,
                                     final ParseException error) {
        super(clazz, input, error.getMessage());
    }

    @Override
    public int getCode() {
        return -40071;
    }
}
