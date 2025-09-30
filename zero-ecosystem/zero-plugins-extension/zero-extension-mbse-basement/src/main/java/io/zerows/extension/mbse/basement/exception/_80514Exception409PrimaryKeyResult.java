package io.zerows.extension.mbse.basement.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80514Exception409PrimaryKeyResult extends VertxWebException {
    public _80514Exception409PrimaryKeyResult(final String name, final String key) {
        super(ERR._80514, name, key);
    }
}
