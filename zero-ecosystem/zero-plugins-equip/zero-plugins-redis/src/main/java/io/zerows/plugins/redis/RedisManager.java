package io.zerows.plugins.redis;

import io.r2mo.typed.cc.Cc;
import io.vertx.redis.client.Redis;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2025-12-31
 */
class RedisManager extends AddOnManager<Redis> {
    private static final Cc<String, Redis> CC_STORED = Cc.open();

    private static final RedisManager INSTANCE = new RedisManager();

    private RedisManager() {
    }

    static RedisManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, Redis> stored() {
        return CC_STORED;
    }
}
