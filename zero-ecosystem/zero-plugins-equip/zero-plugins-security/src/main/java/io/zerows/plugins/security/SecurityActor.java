package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-10-27
 */
@Actor(value = "SECURITY")
public class SecurityActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        return Future.succeededFuture(Boolean.TRUE);
    }
}
