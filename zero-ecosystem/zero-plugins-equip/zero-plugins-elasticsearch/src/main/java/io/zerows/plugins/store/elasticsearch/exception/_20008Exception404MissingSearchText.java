package io.zerows.plugins.store.elasticsearch.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _20008Exception404MissingSearchText extends VertxWebException {

    public _20008Exception404MissingSearchText(final String table) {
        super(ERR._20008, table);
    }
}
