package io.zerows.core.database.jooq.exception;

import io.zerows.core.exception.BootingException;

public class BootJooqCondFieldException extends BootingException {

    public BootJooqCondFieldException(final Class<?> clazz,
                                      final String targetField) {
        super(clazz, targetField);
    }

    @Override
    public int getCode() {
        return -40055;
    }
}
