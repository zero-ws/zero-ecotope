package io.zerows.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

public class _80249Exception409ProviderType extends VertxWebException {
    public _80249Exception409ProviderType(final String provider, final String token) {
        super(ERR._80249, provider, token);
    }
}
