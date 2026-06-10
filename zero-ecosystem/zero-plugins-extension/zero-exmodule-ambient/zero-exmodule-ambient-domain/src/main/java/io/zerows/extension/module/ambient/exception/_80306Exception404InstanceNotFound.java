package io.zerows.extension.module.ambient.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2026-06-08
 */
public class _80306Exception404InstanceNotFound extends VertxWebException {
    public _80306Exception404InstanceNotFound(final String key) {
        super(ERR._80306, key);
    }
}
