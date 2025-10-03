package io.zerows.exception.web;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60025Exception400QrPageIndex extends VertxWebException {
    public _60025Exception400QrPageIndex(final int page) {
        super(ERR._60025, page);
    }
}
