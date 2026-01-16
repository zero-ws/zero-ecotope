package io.zerows.plugins.security.exception;

import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.vertx.common.exception.VertxWebException;

public class _80244Exception401LoginTypeWrong extends VertxWebException {

    public _80244Exception401LoginTypeWrong(final TypeLogin input, final TypeLogin required) {
        super(ERR._80244, input.name(), required.name());
    }
}
