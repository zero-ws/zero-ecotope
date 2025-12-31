package io.zerows.plugins.redis;

import io.vertx.redis.client.Redis;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

/**
 * @author lang : 2025-12-31
 */
class RedisProvider extends AddOnProvider<Redis> {
    RedisProvider(final AddOn<Redis> addOn) {
        super(addOn);
    }
}
