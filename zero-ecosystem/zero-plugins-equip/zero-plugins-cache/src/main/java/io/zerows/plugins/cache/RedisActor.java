package io.zerows.plugins.cache;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-31
 */
@Actor(value = "REDIS", configured = false)
@Slf4j
public class RedisActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        return Future.succeededFuture(Boolean.TRUE);
    }
}
