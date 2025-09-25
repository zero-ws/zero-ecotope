package io.zerows.core.web.model.exception;

import io.zerows.core.exception.BootingException;

public class BootCodexMoreException extends BootingException {

    public BootCodexMoreException(final Class<?> clazz,
                                  final Class<?> target) {
        super(clazz, target);
    }

    @Override
    public int getCode() {
        return -40036;
    }
}
