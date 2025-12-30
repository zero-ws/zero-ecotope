package io.zerows.plugins.elasticsearch.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _20007Exception404MissingIndexName extends VertxWebException {

    public _20007Exception404MissingIndexName(final String table) {
        super(ERR._20007, table);
    }
}
