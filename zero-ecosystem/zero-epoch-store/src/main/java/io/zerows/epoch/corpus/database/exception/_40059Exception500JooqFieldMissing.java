package io.zerows.epoch.corpus.database.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40059Exception500JooqFieldMissing extends VertxBootException {
    public _40059Exception500JooqFieldMissing(final String field,
                                              final Class<?> type) {
        super(ERR._40059, field, type.getName());
    }
}
