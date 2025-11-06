package io.zerows.extension.module.mbsecore.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80538Exception417RelationCounter extends VertxWebException {
    public _80538Exception417RelationCounter(final String identifier,
                                             final Integer schema,
                                             final Integer joins) {
        super(ERR._80538, identifier, schema, joins);
    }
}
