package io.zerows.platform.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.spi.BootIo;

/**
 * @author lang : 2025-09-26
 */
public class _11010Exception500BootIoMissing extends VertxBootException {
    public _11010Exception500BootIoMissing(final Class<?> callerClass) {
        super(ERR._11010, BootIo.class, callerClass.getName());
    }
}
