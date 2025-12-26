package io.zerows.plugins.store.elasticsearch.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _20006Exception404MissingConfiguration extends VertxWebException {

    public _20006Exception404MissingConfiguration(final String table) {
        super(ERR._20006, table);
    }
}
