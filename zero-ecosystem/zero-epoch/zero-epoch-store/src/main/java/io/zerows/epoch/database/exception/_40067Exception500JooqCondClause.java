package io.zerows.epoch.database.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40067Exception500JooqCondClause extends VertxBootException {
    public _40067Exception500JooqCondClause(final String field,
                                            final Class<?> type, final String original) {
        super(ERR._40067, field, type, original);
    }
}
