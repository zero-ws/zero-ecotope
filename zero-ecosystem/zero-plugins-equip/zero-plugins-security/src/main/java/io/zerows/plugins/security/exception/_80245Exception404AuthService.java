package io.zerows.plugins.security.exception;

import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.vertx.common.exception.VertxWebException;

public class _80245Exception404AuthService extends VertxWebException {
    public _80245Exception404AuthService(final TypeLogin type) {
        super(ERR._80245, "UserAt/" + type.name());
    }
}
