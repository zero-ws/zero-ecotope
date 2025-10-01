package io.zerows.epoch.corpus.container.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40030Exception500ParamAnnotation extends VertxBootException {

    public _40030Exception500ParamAnnotation(final String field,
                                             final int occurs) {
        super(ERR._40030, field, occurs);
    }
}
