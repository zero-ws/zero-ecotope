package io.zerows.core.exception.web;

import io.zerows.ams.annotations.Development;
import io.zerows.core.exception.WebException;

public class _400BadRequestException extends WebException {

    public _400BadRequestException(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    public int getCode() {
        return -60011;
    }

    @Development("IDE视图专用")
    private int __60011() {
        return this.getCode();
    }
}
