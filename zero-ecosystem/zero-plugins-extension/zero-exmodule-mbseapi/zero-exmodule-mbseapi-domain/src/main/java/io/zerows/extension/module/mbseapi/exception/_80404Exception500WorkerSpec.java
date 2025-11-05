package io.zerows.extension.module.mbseapi.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80404Exception500WorkerSpec extends VertxWebException {
    public _80404Exception500WorkerSpec(final Class<?> workerCls) {
        super(ERR._80404, workerCls);
    }
}
