package io.zerows.module.metadata.exception;

import io.zerows.core.exception.BootingException;

public class BootDuplicatedImplException extends BootingException {

    public BootDuplicatedImplException(final Class<?> clazz,
                                       final Class<?> interfaceCls) {
        super(clazz, interfaceCls.getName());
    }

    @Override
    public int getCode() {
        return -40028;
    }
}
