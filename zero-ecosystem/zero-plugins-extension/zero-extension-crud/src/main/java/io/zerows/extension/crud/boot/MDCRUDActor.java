package io.zerows.extension.crud.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.boot.ExAbstractHActor;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "extension", sequence = 200)
public class MDCRUDActor extends ExAbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        return Future.succeededFuture(Boolean.TRUE);
    }
}
