package io.zerows.core.web.security.exception;

import io.zerows.core.exception.BootingException;

public class BootWallProviderConflictException extends BootingException {

    public BootWallProviderConflictException(final Class<?> clazz,
                                             final Class<?> target) {
        super(clazz, target);
    }

    @Override
    public int getCode() {
        return -40077;
    }
}
