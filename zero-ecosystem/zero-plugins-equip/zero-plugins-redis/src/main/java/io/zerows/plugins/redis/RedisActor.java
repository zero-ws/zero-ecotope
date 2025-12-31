package io.zerows.plugins.redis;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-31
 */
@Actor(value = "REDIS", sequence = -188)
@Slf4j
public class RedisActor extends AbstractHActor {

    public static Redis ofClient() {
        return RedisAddOn.of().createSingleton();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        return Future.succeededFuture(Boolean.TRUE);
    }
}
