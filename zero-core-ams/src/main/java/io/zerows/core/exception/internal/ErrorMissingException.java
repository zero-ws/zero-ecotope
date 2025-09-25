package io.zerows.core.exception.internal;

import io.zerows.ams.util.HUt;
import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.error.ErrorCode;
import io.zerows.core.exception.InternalException;

public class ErrorMissingException extends InternalException {

    public ErrorMissingException(final Class<?> caller, final Integer code) {
        super(caller, HUt.fromMessage(ErrorCode._11003.M(), String.valueOf(code)));
    }

    @Override
    protected int getCode() {
        return ErrorCode._11003.V();
    }

    @Development("IDE视图专用")
    private int __11003() {
        return this.getCode();
    }
}
