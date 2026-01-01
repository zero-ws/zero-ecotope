package io.zerows.plugins.redis;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import io.zerows.sdk.plugins.AddOnVertx;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2026-01-01
 */
class RedisAddOn extends AddOnVertx<Redis> {
    private static final String NAME_ADDON = "AddOn/DEFAULT";
    private static RedisAddOn INSTANCE;

    private RedisAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static RedisAddOn of() {
        return INSTANCE;
    }

    @CanIgnoreReturnValue
    static RedisAddOn of(final Vertx vertx, final HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new RedisAddOn(vertx, config);
        }
        return INSTANCE;
    }

    @Override
    protected String name() {
        return NAME_ADDON;
    }

    @Override
    protected RedisManager manager() {
        return RedisManager.of();
    }

    @Override
    protected Redis createInstanceBy(final String name) {
        final JsonObject options = this.config().options();
        final RedisOptions redisOptions = new RedisOptions(options);
        /*
         * TODO: 扩展异步模式 -> 后期分布式使用
         * Redis.createStandaloneClient()
         * Redis.createReplicationClient()
         * Redis.createSentinelClient()
         * Redis.createClusterClient()
         */
        return Redis.createClient(this.vertx(), redisOptions);
    }
}
