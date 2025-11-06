package io.zerows.extension.module.mbsecore.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80529Exception404RecordMissing extends VertxWebException {
    public _80529Exception404RecordMissing(final String key) {
        super(ERR._80529, key);
    }
}
