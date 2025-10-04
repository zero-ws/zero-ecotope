package io.zerows.corpus.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.epoch.basicore.ActorEvent;

/**
 * @author lang : 2025-09-30
 */
public class _40008Exception500EventActionNone extends VertxBootException {

    public _40008Exception500EventActionNone(final ActorEvent event) {
        super(ERR._40008, event.getPath());
    }
}
