package io.zerows.epoch.based.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.epoch.enums.EmSecure;

/**
 * @author lang : 2025-09-30
 */
public class _80224Exception409UiPhaseEager extends VertxWebException {
    public _80224Exception409UiPhaseEager(final EmSecure.ActPhase phase) {
        super(ERR._80224, phase.name());
    }
}
