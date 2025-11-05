package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80202Exception401CodeGeneration extends VertxWebException {

    public _80202Exception401CodeGeneration(final String clientId,
                                            final String clientSecret) {
        super(ERR._80202, clientId, clientSecret);
    }
}
