package io.zerows.platform.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _30001Exception500ServerConfig extends VertxBootException {
    public _30001Exception500ServerConfig(final String configKey) {
        super(ERR._30001, configKey);
    }
}
