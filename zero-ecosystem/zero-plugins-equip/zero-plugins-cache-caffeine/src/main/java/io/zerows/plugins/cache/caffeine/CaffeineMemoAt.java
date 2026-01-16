package io.zerows.plugins.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtBase;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.plugins.cache.CachedConstant;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class CaffeineMemoAt<K, V> extends MemoAtBase<K, V> {

    private static final Cc<String, Cache<?, ?>> CC_CACHE = Cc.open();
    // 持有 Caffeine 的核心 Cache 实例
    private final Cache<K, V> cache;

    @SuppressWarnings("unchecked")
    protected CaffeineMemoAt(final Vertx vertxRef, final MemoOptions<K, V> options) {
        super(vertxRef, options);
        // 在构造时直接初始化缓存实例
        this.cache = (Cache<K, V>) CC_CACHE.pick(() -> this.initCache(options), options.fingerprint());
    }

    /**
     * 根据配置构建 Caffeine 实例
     */
    private Cache<K, V> initCache(final MemoOptions<K, V> options) {
        // 1. 获取配置，如果在 Factory 中已经 setConfiguration，这里可以直接强转
        final CaffeineYmConfig config = options.configuration();

        // 2. 初始化构建器
        final Caffeine<Object, Object> builder = Caffeine.newBuilder();

        if (Objects.nonNull(config)) {
            // --- 容量控制 ---
            if (Objects.nonNull(config.getInitialCapacity()) && config.getInitialCapacity() > 0) {
                builder.initialCapacity(config.getInitialCapacity());
            }
            if (Objects.nonNull(config.getMaximumSize()) && config.getMaximumSize() > 0) {
                builder.maximumSize(config.getMaximumSize());
            }

            // --- 时间控制 ---
            // 写入过期 (TTL)
            final Duration expiredAt = config.expiredAt();
            if (Objects.nonNull(expiredAt) && !expiredAt.isZero()) {
                builder.expireAfterWrite(expiredAt);
            }
            // 访问过期 (Idle)
            final Duration idleAt = config.idleAt();
            if (Objects.nonNull(idleAt) && !idleAt.isZero()) {
                builder.expireAfterAccess(idleAt);
            }

            // --- 引用类型 (GC 策略) ---
            if (Boolean.TRUE.equals(config.getWeakKeys())) {
                builder.weakKeys();
            }
            if (Boolean.TRUE.equals(config.getWeakValues())) {
                builder.weakValues();
            }
            if (Boolean.TRUE.equals(config.getSoftValues())) {
                builder.softValues();
            }

            // --- 监控 ---
            if (Boolean.TRUE.equals(config.getRecordStats())) {
                builder.recordStats();
            }
        } else {
            // 兜底策略：如果配置为空，使用 MemoOptions 的基础配置
            if (options.size() > 0) {
                builder.maximumSize(options.size());
            }
            if (options.duration() != null && !options.duration().isZero()) {
                builder.expireAfterWrite(options.duration());
            }
        }

        log.info("{} Caffeine 缓存构造完成: Fingerprint={}, Config={}",
            CachedConstant.K_PREFIX_CACHE, options.fingerprint(), config);
        return builder.build();
    }

    // ---------------- Interface Implementation ----------------

    @Override
    public Future<Kv<K, V>> put(final K key, final V value) {
        if (key == null || value == null) {
            // Caffeine 不允许 Key 或 Value 为 null
            return Future.failedFuture(new IllegalArgumentException("Caffeine cache does not support null keys or values."));
        }
        this.cache.put(key, value);
        return Future.succeededFuture(Kv.create(key, value));
    }

    @Override
    public Future<Kv<K, V>> remove(final K key) {
        if (key != null) {
            this.cache.invalidate(key);
        }
        // Caffeine invalidate 无返回值，返回 Key + Null 表示移除成功
        return Future.succeededFuture(Kv.create(key, null));
    }

    @Override
    public Future<V> find(final K key) {
        if (key == null) {
            return Future.succeededFuture();
        }
        final V value = this.cache.getIfPresent(key);
        return Future.succeededFuture(value); // value 为 null 时表示未命中，Future 依然是 succeeded
    }

    @Override
    public Future<Boolean> clear() {
        this.cache.invalidateAll();
        return Future.succeededFuture(Boolean.TRUE);
    }

    @Override
    public Future<Set<K>> keySet() {
        // cache.asMap().keySet() 返回的是视图，为了线程安全和避免并发修改异常，建议拷贝一份快照
        // 但对于超大数据量的缓存，这一步可能会有性能损耗，需根据实际场景权衡
        final Set<K> keys = new HashSet<>(this.cache.asMap().keySet());
        return Future.succeededFuture(keys);
    }

    @Override
    public Future<Integer> size() {
        // estimatedSize 是近似值，计算精确值需要加锁遍历，对于缓存监控来说近似值足够了
        // 如果 long 超过 int 范围，强转为 MAX_VALUE
        final long estimated = this.cache.estimatedSize();
        final int size = (estimated > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) estimated;
        return Future.succeededFuture(size);
    }
}