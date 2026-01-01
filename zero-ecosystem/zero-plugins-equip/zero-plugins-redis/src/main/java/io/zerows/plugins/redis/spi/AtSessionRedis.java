package io.zerows.plugins.redis.spi;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.sstore.redis.RedisSessionStore;
import io.vertx.redis.client.Redis;
import io.zerows.cortex.sdk.AtSession;
import io.zerows.plugins.redis.RedisActor;

import java.util.Objects;

/**
 * @author lang : 2025-12-31
 */
@SPID("AtSession/SPI")
public class AtSessionRedis implements AtSession {
    @Override
    public Future<SessionStore> createStore(final Vertx vertx, final JsonObject session) {
        // 1. 获取全局单例的 Redis 客户端
        final Redis redis = RedisActor.ofClient();

        // [安全检查] 防止 Redis 插件未启用或初始化失败导致空指针
        if (Objects.isNull(redis)) {
            return Future.failedFuture(new RuntimeException("[ Zero ] Redis 客户端未初始化! 请检查您的配置."));
        }

        // 2. 创建 RedisSessionStore
        // Vert.x 4.x/5.x 中，create 是同步方法，直接返回 store 实例
        final SessionStore store = RedisSessionStore.create(vertx, redis);

        // 3. 包装为 Future 返回
        return Future.succeededFuture(store);
    }
}
