package io.zerows.core.web.invocation.exception;

import io.zerows.core.exception.BootingException;

public class BootAsyncSignatureException extends BootingException {

    public BootAsyncSignatureException(final Class<?> clazz,
                                       final String returnType,
                                       final String paramType) {
        super(clazz, returnType, paramType);
    }

    @Override
    public int getCode() {
        return -40018;
    }
}
