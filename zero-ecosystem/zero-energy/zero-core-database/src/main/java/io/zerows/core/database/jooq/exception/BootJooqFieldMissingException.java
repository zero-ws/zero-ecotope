package io.zerows.core.database.jooq.exception;

import io.zerows.core.exception.BootingException;

public class BootJooqFieldMissingException extends BootingException {

    public BootJooqFieldMissingException(
        final Class<?> clazz,
        final String field,
        final Class<?> type) {
        super(clazz, field, type.getName());
    }

    @Override
    public int getCode() {
        return -40059;
    }
}
