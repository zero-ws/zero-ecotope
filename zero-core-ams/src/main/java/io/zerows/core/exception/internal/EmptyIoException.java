package io.zerows.core.exception.internal;

import io.zerows.ams.util.HUt;
import io.zerows.ams.annotations.Development;
import io.zerows.ams.constant.error.ErrorCode;
import io.zerows.core.exception.InternalException;

public class EmptyIoException extends InternalException {

    public EmptyIoException(final Class<?> caller, final String filename) {
        super(caller, HUt.fromMessage(ErrorCode._11002.M(), filename));
    }

    @Override
    protected int getCode() {
        return ErrorCode._11002.V();
    }

    @Development("IDE视图专用")
    private int __11002() {
        return this.getCode();
    }
}
