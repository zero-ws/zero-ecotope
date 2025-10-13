package io.zerows.component.module;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.specification.configuration.HActor;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-10-13
 */
public abstract class AbstractHActor implements HActor {
    @Override
    public <T> Future<Boolean> startAsync(final HConfig config, final T containerRef) {
        
        return Future.succeededFuture(Boolean.TRUE);
    }

    protected abstract Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef);
}
