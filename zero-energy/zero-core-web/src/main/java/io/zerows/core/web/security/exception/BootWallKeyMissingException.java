package io.zerows.core.web.security.exception;

import io.zerows.core.exception.BootingException;

public class BootWallKeyMissingException extends BootingException {

    public BootWallKeyMissingException(final Class<?> clazz,
                                       final String key,
                                       final Class<?> target) {
        super(clazz, key, target);
    }

    @Override
    public int getCode() {
        return -40040;
    }
}
