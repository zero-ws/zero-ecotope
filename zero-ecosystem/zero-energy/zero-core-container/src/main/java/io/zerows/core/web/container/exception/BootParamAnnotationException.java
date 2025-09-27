package io.zerows.core.web.container.exception;

import io.zerows.core.exception.BootingException;

public class BootParamAnnotationException extends BootingException {

    public BootParamAnnotationException(final Class<?> clazz,
                                        final String field,
                                        final int occurs) {
        super(clazz, field, occurs);
    }

    @Override
    public int getCode() {
        return -40030;
    }
}
