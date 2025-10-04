package io.zerows.corpus.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40014Exception500WorkerMissing extends VertxBootException {
    public _40014Exception500WorkerMissing(final String address) {
        super(ERR._40014, address);
    }
}
