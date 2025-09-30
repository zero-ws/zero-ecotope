package io.zerows.extension.runtime.ambient.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80304Exception409TrackableConflict extends VertxWebException {
    public _80304Exception409TrackableConflict(final String trackableId) {
        super(ERR._80304, trackableId);
    }
}
