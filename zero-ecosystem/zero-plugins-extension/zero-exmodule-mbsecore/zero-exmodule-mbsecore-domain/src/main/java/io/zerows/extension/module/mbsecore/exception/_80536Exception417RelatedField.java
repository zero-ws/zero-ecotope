package io.zerows.extension.module.mbsecore.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80536Exception417RelatedField extends VertxWebException {
    public _80536Exception417RelatedField(final String entityKey,
                                          final String entity,
                                          final String unique) {
        super(ERR._80536, entityKey, entity, unique);
    }
}
