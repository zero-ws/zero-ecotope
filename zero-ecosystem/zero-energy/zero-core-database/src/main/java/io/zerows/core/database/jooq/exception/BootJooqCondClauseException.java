package io.zerows.core.database.jooq.exception;

import io.zerows.core.exception.BootingException;

public class BootJooqCondClauseException extends BootingException {
    public BootJooqCondClauseException(final Class<?> clazz, final String field,
                                       final Class<?> type, final String original) {
        super(clazz, field, type, original);
    }

    @Override
    public int getCode() {
        return -40067;
    }
}
