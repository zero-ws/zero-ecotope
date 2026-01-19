package io.zerows.plugins.security.weco.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-12-11
 */
public class _81554Exception401WeComBlocked extends VertxWebException {
    public _81554Exception401WeComBlocked(final String url) {
        super(ERR._81554, url);
    }
}
