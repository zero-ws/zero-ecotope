package io.zerows.core.exception.boot;

import io.zerows.ams.annotations.Development;
import io.zerows.core.exception.BootingException;

public class CombineOwnerException extends BootingException {
    public CombineOwnerException(final Class<?> clazz,
                                 final String id,
                                 final String targetId) {
        super(clazz, targetId);
    }

    @Override
    public int getCode() {
        return -40102;
    }

    @Development
    private int _40102() {
        return this.getCode();
    }
}
