package io.zerows.core.exception.internal;

import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.error.ErrorCode;
import io.zerows.core.exception.InternalException;

/**
 * @author lang : 2023-05-30
 */
public class BootIoMissingException extends InternalException {
    public BootIoMissingException(final Class<?> caller) {
        super(caller);
    }

    @Override
    protected int getCode() {
        return ErrorCode._11010.V();
    }

    @Development
    private int __11010() {
        return this.getCode();
    }
}
