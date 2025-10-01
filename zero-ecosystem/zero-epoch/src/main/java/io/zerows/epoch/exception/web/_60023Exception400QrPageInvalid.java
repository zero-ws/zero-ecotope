package io.zerows.epoch.exception.web;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60023Exception400QrPageInvalid extends VertxWebException {
    public _60023Exception400QrPageInvalid(final String page) {
        super(ERR._60023, page);
    }
}
