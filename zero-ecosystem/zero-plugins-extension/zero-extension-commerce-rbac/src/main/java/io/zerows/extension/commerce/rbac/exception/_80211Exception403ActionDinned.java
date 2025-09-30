package io.zerows.extension.commerce.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80211Exception403ActionDinned extends VertxWebException {
    public _80211Exception403ActionDinned(final int expected, final int current) {
        super(ERR._80211, expected, current);
    }
}
