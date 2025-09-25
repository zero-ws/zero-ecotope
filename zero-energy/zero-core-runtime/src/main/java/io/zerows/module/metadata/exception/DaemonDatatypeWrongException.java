package io.zerows.module.metadata.exception;

import io.zerows.ams.constant.em.typed.EmType;
import io.zerows.core.exception.DaemonException;

public class DaemonDatatypeWrongException extends DaemonException {

    public DaemonDatatypeWrongException(final Class<?> clazz,
                                        final String field,
                                        final Object value,
                                        final EmType.Json type) {
        super(clazz, field, value, type);
    }

    @Override
    public int getCode() {
        return -10003;
    }
}
