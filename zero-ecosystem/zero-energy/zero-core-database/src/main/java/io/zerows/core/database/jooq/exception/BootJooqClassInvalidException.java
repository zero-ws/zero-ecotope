package io.zerows.core.database.jooq.exception;

import io.zerows.core.exception.BootingException;

public class BootJooqClassInvalidException extends BootingException {
    public BootJooqClassInvalidException(final Class<?> clazz, final String name) {
        super(clazz, name);
    }

    @Override
    public int getCode() {
        return -40066;
    }
}
