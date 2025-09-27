package io.zerows.core.database.jooq.exception;

import io.zerows.core.exception.BootingException;

public class BootJooqMergeException extends BootingException {

    public BootJooqMergeException(final Class<?> clazz,
                                  final Class<?> target,
                                  final String data) {
        super(clazz, target, data);
    }

    @Override
    public int getCode() {
        return -40057;
    }
}
