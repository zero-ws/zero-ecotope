package io.zerows.plugins.integration.sms.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

import java.util.Objects;

/**
 * @author lang : 2025-09-30
 */
public class _20004Exception424MessageSend extends VertxWebException {
    public _20004Exception424MessageSend(final Throwable ex) {
        super(ERR._20004, Objects.requireNonNull(ex).getMessage());
    }
}
