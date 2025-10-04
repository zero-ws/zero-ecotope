package io.zerows.corpus.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80510Exception404ModelNotFound extends VertxWebException {
    public _80510Exception404ModelNotFound(final String namespace,
                                           final String identifier) {
        super(ERR._80510, namespace, identifier);
    }
}
