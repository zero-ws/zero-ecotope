package io.zerows.plugins.redis.cache;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.sstore.redis.RedisSessionStore;
import io.vertx.redis.client.Redis;
import io.zerows.cortex.sdk.AtSession;
import io.zerows.plugins.redis.RedisActor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-12-31
 */
@SPID("AtSession/SPI")
@Slf4j
public class RedisAtSession implements AtSession {

    // 静态锁对象，所有实例共用这一把锁
    private static final Object GLOBAL_LOCK = new Object();
    // ==========================================================
    // 关键修改：全部改为 static，保证 JVM 级别唯一，无视多实例问题
    // ==========================================================
    private static volatile boolean IS_INITIALIZED = false;
    private static volatile SessionStore GLOBAL_STORE;

    @Override
    public Future<SessionStore> createStore(final Vertx vertx, final JsonObject session) {
        // 1. 【第一重检查】(静态变量)
        if (IS_INITIALIZED) {
            return Future.succeededFuture(GLOBAL_STORE);
        }

        // 2. 【锁】锁住全局唯一的静态对象
        synchronized (GLOBAL_LOCK) {
            // 3. 【第二重检查】
            if (IS_INITIALIZED) {
                return Future.succeededFuture(GLOBAL_STORE);
            }

            // --- 初始化逻辑 ---
            final Redis redis = RedisActor.ofClient();

            if (Objects.isNull(redis)) {
                // 标记为已初始化，防止后续线程重复打印日志
                // 哪怕失败了，也算"尝试过初始化了"
                IS_INITIALIZED = true;
                log.warn("[ PLUG ] ( Session ) Redis 客户端未就绪或未配置，RedisSessionStore 将不可用.");
                return Future.succeededFuture();
            }

            try {
                // 创建 Store
                GLOBAL_STORE = RedisSessionStore.create(vertx, redis);
                log.info("[ PLUG ] ( Session ) RedisSessionStore 全局初始化成功. Vertx = {}", vertx.hashCode());
            } catch (final Exception e) {
                log.error("[ PLUG ] ( Session ) RedisSessionStore 初始化异常", e);
            } finally {
                // 无论成功还是异常，都标记完成，避免死循环重试
                IS_INITIALIZED = true;
            }
        }

        return Future.succeededFuture(GLOBAL_STORE);
    }
}