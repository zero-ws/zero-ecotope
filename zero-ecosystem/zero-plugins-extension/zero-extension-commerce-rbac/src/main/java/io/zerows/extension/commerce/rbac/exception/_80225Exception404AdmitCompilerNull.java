package io.zerows.extension.commerce.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.platform.enums.EmSecure;

/**
 * @author lang : 2025-09-30
 */
public class _80225Exception404AdmitCompilerNull extends VertxWebException {
    public _80225Exception404AdmitCompilerNull(final EmSecure.ScIn in) {
        super(ERR._80225, in.name());
    }
}
