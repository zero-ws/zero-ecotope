package io.zerows.epoch.corpus.container.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.epoch.basicore.Event;

/**
 * @author lang : 2025-09-30
 */
public class _40008Exception500EventActionNone extends VertxBootException {

    public _40008Exception500EventActionNone(final Event event) {
        super(ERR._40008, event.getPath());
    }
}
