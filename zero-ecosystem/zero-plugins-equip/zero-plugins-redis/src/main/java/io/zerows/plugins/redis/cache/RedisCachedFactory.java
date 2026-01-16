package io.zerows.plugins.redis.cache;

import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.cache.CachedFactory;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Redis 缓存工厂实现
 * 优先级设为 100，通常低于内存缓存（如 Caffeine/EhCache），
 * 适用于分布式共享缓存场景。
 */
@Slf4j
@SPID(priority = 100)
public class RedisCachedFactory implements CachedFactory {

    // 实例级 Memo 复用池，Key 为 fingerprint
    private static final Cc<String, MemoAt<?, ?>> CC_MEMO = Cc.openThread();

    @Override
    public <K, V> MemoAt<K, V> findConfigured(final Vertx vertx, final MemoOptions<K, V> options) {
        // 1. 解析配置
        final RedisYmConfig config = this.configOf(options);
        if (Objects.isNull(config)) {
            log.warn("[ R2MO ] Redis 配置缺失，无法构造此类 MemoAt，请检查：{}", options.extension());
            return null;
        }

        // 2. 构造新的 MemoOptions
        // Redis 强依赖 TTL，这里将配置中的 expiredAt 注入到 options 中
        final MemoOptions<K, V> optionsUpdated = options.of(config.expiredAt());

        // 将完整的 RedisYmConfig 注入，以便 RedisMemoAt 获取 prefix, nullValue 等配置
        optionsUpdated.configuration(config);

        return this.findBy(vertx, optionsUpdated);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> MemoAt<K, V> findBy(final Vertx vertx, final MemoOptions<K, V> options) {
        Objects.requireNonNull(options, "[ R2MO ] MemoOptions 不能为空！");
        // 指纹会包含 options 中的关键信息，确保配置变更后能生成新实例
        final String fingerprint = options.fingerprint();

        return (MemoAt<K, V>) CC_MEMO.pick(
            () -> new RedisMemoAt<>(vertx, options),
            fingerprint
        );
    }

    /**
     * 从 options 的扩展配置中提取 redis 节点配置
     */
    private <K, V> RedisYmConfig configOf(final MemoOptions<K, V> options) {
        Objects.requireNonNull(options);
        final JsonObject extension = options.extension();
        if (Objects.isNull(extension)) {
            return null;
        }
        // 读取 "redis" 节点: cache -> redis
        final JsonObject optionJ = Ut.valueJObject(extension, "redis");
        return Ut.deserialize(optionJ, RedisYmConfig.class);
    }
}