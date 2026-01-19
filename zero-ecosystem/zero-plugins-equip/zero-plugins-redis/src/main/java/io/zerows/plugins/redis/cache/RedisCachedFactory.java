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
        final MemoOptions<K, V> optionsUpdated = options.of(options.duration());

        // 将完整的 RedisYmConfig 注入，以便 RedisMemoAt 获取 prefix, nullValue 等配置
        optionsUpdated.configuration(config);

        return this.findBy(vertx, optionsUpdated);
    }

    /**
     * <pre>
     * 🟢 构造 Redis 缓存组件
     *
     * 1. 🌐 为何追加 duration 到 fingerprint？
     *    Redis 缓存组件在执行 SET 操作时，严强依赖配置中的 `TTL` (Time To Live)。
     *    `CC_MEMO` 作为一个静态内存池，用于复用 `MemoAt` 实例以减少对象创建开销。
     *    如果不将 duration 包含在从池中查找实例的 key (指纹) 中：
     *
     *    - ❌ 场景重现：
     *      1. 模块 A 创建了名为 "UserCache" 的实例，TTL 配置为 60秒。
     *      2. 模块 B 尝试获取名为 "UserCache" 的实例，TTL 配置为 3600秒 (1小时)。
     *      3. 结果：模块 B 会错误地复用模块 A 创建的实例（因为名字相同）。
     *      4. 后果：模块 B 存入的数据将在 60秒后失效，而不是预期的 1小时，导致严重的业务逻辑错误 (Cache Miss)。
     *
     *    - ✅ 解决方案：
     *      Redis 组件的唯一性指纹必须由 `逻辑名称` + `过期时间` 共同决定。
     *      Fingerprint = Name + "@" + Duration_Millis
     *
     * 2. 🎯 缓存池机制
     *    利用 `CC_MEMO` 避免重复创建 RedisClient 包装器或重配置开销，但在多 TTL 场景下保持实例隔离。
     * </pre>
     *
     * @param vertx   Vert.x 实例
     * @param options 缓存配置选项
     * @return Redis 缓存操作接口
     */
    @Override
    @SuppressWarnings("unchecked")
    public <K, V> MemoAt<K, V> findBy(final Vertx vertx, final MemoOptions<K, V> options) {
        Objects.requireNonNull(options, "[ R2MO ] MemoOptions 不能为空！");
        // 指纹会包含 options 中的关键信息，确保配置变更后能生成新实例
        // Fix: 追加 Duration 作为 fingerprint，因为 Redis 强依赖 TTL
        final String fingerprint = options.fingerprint() + "@" + options.duration().toMillis();
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