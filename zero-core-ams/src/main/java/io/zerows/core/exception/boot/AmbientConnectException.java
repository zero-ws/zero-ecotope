package io.zerows.core.exception.boot;

import io.zerows.ams.annotations.Development;
import io.zerows.core.exception.BootingException;

public class AmbientConnectException extends BootingException {

    public AmbientConnectException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -40103;
    }

    @Development
    private int _40103() {
        return this.getCode();
    }
}
