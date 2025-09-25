package io.zerows.core.exception.internal;

import io.zerows.ams.util.HUt;
import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.error.ErrorCode;
import io.zerows.core.exception.InternalException;

public class OperationException extends InternalException {
    public OperationException(final Class<?> caller, final String method) {
        super(caller, HUt.fromMessage(ErrorCode._11005.M(), method, caller.getName()));
    }

    public OperationException(final Class<?> caller, final String method, final Class<?> clazz) {
        super(caller, HUt.fromMessage(ErrorCode._11005.M(), method, clazz.getName()));
    }

    @Override
    protected int getCode() {
        return ErrorCode._11005.V();
    }

    @Development("IDE视图专用")
    private int __11005() {
        return this.getCode();
    }
}
