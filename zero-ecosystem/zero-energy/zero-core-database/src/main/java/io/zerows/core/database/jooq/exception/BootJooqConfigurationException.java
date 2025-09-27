package io.zerows.core.database.jooq.exception;

import io.zerows.core.exception.BootingException;

public class BootJooqConfigurationException extends BootingException {

    public BootJooqConfigurationException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -40065;
    }
}
