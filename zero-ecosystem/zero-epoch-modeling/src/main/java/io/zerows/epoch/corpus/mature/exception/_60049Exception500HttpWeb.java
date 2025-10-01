package io.zerows.epoch.corpus.mature.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.vertx.ext.web.handler.HttpException;

/**
 * @author lang : 2025-09-30
 */
public class _60049Exception500HttpWeb extends VertxWebException {
    public _60049Exception500HttpWeb(final HttpException ex) {
        super(ERR._60049, ex.getStatusCode(), ex.getMessage());
    }
}
