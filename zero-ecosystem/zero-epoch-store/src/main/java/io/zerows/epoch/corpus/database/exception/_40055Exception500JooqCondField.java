package io.zerows.epoch.corpus.database.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40055Exception500JooqCondField extends VertxBootException {
    public _40055Exception500JooqCondField(final String targetField) {
        super(ERR._40055, targetField);
    }
}
