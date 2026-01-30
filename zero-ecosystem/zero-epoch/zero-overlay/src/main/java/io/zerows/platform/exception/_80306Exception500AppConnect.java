package io.zerows.platform.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80306Exception500AppConnect extends VertxWebException {
    public _80306Exception500AppConnect(final String connectBy, final String valueIn, final String valueDb) {
        super(ERR._80306, connectBy, valueIn, valueDb);
    }
}
