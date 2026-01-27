package io.zerows.epoch.spec.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

public class _41003Exception500ConfigMissing extends VertxBootException {
    public _41003Exception500ConfigMissing(final String configPath) {
        super(ERR._41003, configPath);
    }
}
