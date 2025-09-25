package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

public class BootFilterOrderException extends BootingException {

    public BootFilterOrderException(final Class<?> clazz,
                                    final Class<?> filterCls) {
        super(clazz, filterCls);
    }

    @Override
    public int getCode() {
        return -40053;
    }
}
