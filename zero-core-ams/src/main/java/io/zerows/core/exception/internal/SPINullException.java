package io.zerows.core.exception.internal;

import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.error.ErrorCode;
import io.zerows.core.exception.InternalException;

/**
 * @author lang : 2023/4/27
 */
public class SPINullException extends InternalException {

    public SPINullException(final Class<?> clazz) {
        super(clazz, ErrorCode._11000.M());
    }

    @Override
    protected int getCode() {
        return ErrorCode._11000.V();
    }

    @Development("IDE视图专用")
    private int __11000() {
        return this.getCode();
    }
}
