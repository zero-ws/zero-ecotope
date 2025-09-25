package io.zerows.core.web.security.exception;

import io.zerows.core.exception.BootingException;

public class BootWallTypeWrongException extends BootingException {

    public BootWallTypeWrongException(final Class<?> clazz,
                                      final String key,
                                      final Class<?> target) {
        super(clazz, key, target);
    }

    @Override
    public int getCode() {
        return -40075;
    }
}
