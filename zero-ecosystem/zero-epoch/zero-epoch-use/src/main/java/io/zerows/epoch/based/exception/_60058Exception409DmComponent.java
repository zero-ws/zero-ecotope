package io.zerows.epoch.based.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.enums.EmSecure;

/**
 * @author lang : 2025-09-30
 */
public class _60058Exception409DmComponent extends VertxWebException {
    public _60058Exception409DmComponent(final EmSecure.ScDim dim) {
        super(ERR._60058, dim.name());
    }
}
