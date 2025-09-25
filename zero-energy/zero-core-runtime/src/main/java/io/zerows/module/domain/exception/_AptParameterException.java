package io.zerows.module.domain.exception;

import io.zerows.core.exception.InternalException;

public class _AptParameterException extends InternalException {

    private static final String PATTERN = "The arguments could not be both null to build `Apt` component";

    public _AptParameterException(final Class<?> caller) {
        super(caller, PATTERN);
    }

    @Override
    public int getCode() {
        return -15000;
    }
}
