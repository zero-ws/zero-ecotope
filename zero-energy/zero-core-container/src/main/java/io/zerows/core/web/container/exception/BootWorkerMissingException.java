package io.zerows.core.web.container.exception;

import io.zerows.core.exception.BootingException;

public class BootWorkerMissingException extends BootingException {

    public BootWorkerMissingException(final Class<?> clazz,
                                      final String address) {
        super(clazz, address);
    }

    @Override
    public int getCode() {
        return -40014;
    }
}
