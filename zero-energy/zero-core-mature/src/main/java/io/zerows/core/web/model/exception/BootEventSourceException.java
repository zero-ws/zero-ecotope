package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

public class BootEventSourceException extends BootingException {

    public BootEventSourceException(final Class<?> clazz,
                                    final String endpointCls) {
        super(clazz, endpointCls);
    }

    @Override
    public int getCode() {
        return -40005;
    }
}
