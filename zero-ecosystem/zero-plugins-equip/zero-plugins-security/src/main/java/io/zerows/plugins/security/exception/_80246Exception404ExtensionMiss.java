package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80246Exception404ExtensionMiss extends VertxWebException {

    public _80246Exception404ExtensionMiss(final String authorization) {
        super(ERR._80246, authorization);
    }
}
