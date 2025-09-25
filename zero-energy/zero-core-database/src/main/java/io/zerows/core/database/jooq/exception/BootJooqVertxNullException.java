package io.zerows.core.database.jooq.exception;

import io.zerows.core.exception.BootingException;

public class BootJooqVertxNullException extends BootingException {

    public BootJooqVertxNullException(
        final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -40060;
    }
}
