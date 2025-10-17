package io.zerows.epoch.store;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-10-17
 */
@Actor(value = "DATABASE", sequence = -1017)
public class DSActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        return Future.succeededFuture(Boolean.TRUE);
    }
}
