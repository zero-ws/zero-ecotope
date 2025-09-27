package io.zerows.core.web.security.exception;

import io.zerows.core.exception.BootingException;

public class BootWallMethodDuplicatedException extends BootingException {

    public BootWallMethodDuplicatedException(final Class<?> clazz,
                                             final String annoCls,
                                             final String targetCls) {
        super(clazz, annoCls, targetCls);
    }

    @Override
    public int getCode() {
        return -40041;
    }
}
