package io.zerows.plugins.cache.ehcache;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtBase;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.plugins.cache.CachedConstant;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class EhCacheMemoAt<K, V> extends MemoAtBase<K, V> {

    // 全局静态 CacheManager 池
    private static final Cc<String, CacheManager> CC_MANAGER = Cc.open();

    // 实例级 Cache 复用池：确保同一个 Memo 实例中 getCache 拿到的是同一个引用
    private final Cc<String, Cache<K, V>> CC_CACHE = Cc.open();

    private final CacheManager managerOf;

    /**
     * 参考 {@link MemoAtBase} 中的注释，数据结构和设计结构是一致的
     *
     * @param vertxRef 容器引用器
     * @param options  缓存配置项
     */
    protected EhCacheMemoAt(final Vertx vertxRef, final MemoOptions<K, V> options) {
        super(vertxRef, options);
        final String fingerprint = options.fingerprint();
        // build(true) 确保 Manager 初始化
        this.managerOf = CC_MANAGER.pick(() -> CacheManagerBuilder.newCacheManagerBuilder().build(true), fingerprint);
        log.info("{} CacheManager 构造完成：{}, Hash = {}", CachedConstant.K_PREFIX_CACHE, fingerprint, this.managerOf.hashCode());
    }

    /**
     * 获取缓存实例
     * 注意：这里去掉了方法级别的 <K,V> 声明，直接使用类的泛型，防止泛型遮蔽(Shadowing)
     */
    @SuppressWarnings("unchecked")
    protected Cache<K, V> getCache() {
        final MemoOptions<K, V> options = this.options();
        final String cacheName = options.name(); // 建议使用 unique 的 name，或者 fingerprint

        return this.CC_CACHE.pick(() -> {
            // 1. 确定类型 (延迟构造核心逻辑)
            // 兼容旧版：如果未显式指定类型，则使用 Object.class
            final Class<K> kType = options.classK() != null ? options.classK() : (Class<K>) Object.class;
            final Class<V> vType = options.classV() != null ? options.classV() : (Class<V>) Object.class;

            // 2. 检查 CacheManager 中是否已存在该 Cache
            final Cache<K, V> existing = this.managerOf.getCache(cacheName, kType, vType);
            if (existing != null) {
                return existing;
            }

            // 3. 构建配置
            final Duration expiredAt = options.duration();
            final int size = options.size() > 0 ? options.size() : 100; // 默认给100防止报错

            log.info("{} 初始化 EhCache 实例: Name={}, Size={}, TTL={}, KeyType={}, ValueType={}",
                CachedConstant.K_PREFIX_CACHE, cacheName, size, expiredAt, kType.getSimpleName(), vType.getSimpleName());

            CacheConfigurationBuilder<K, V> configBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(
                kType, vType, ResourcePoolsBuilder.heap(size));

            // 4. 设置过期策略 (如果有)
            if (expiredAt != null && !expiredAt.isZero()) {
                configBuilder = configBuilder.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(expiredAt));
            }

            // 5. 创建 Cache
            return this.managerOf.createCache(cacheName, configBuilder.build());
        }, cacheName);
    }

    @Override
    public Future<Kv<K, V>> put(final K key, final V value) {
        try {
            this.getCache().put(key, value);
            return Future.succeededFuture(Kv.create(key, value));
        } catch (final Exception e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Future<Kv<K, V>> remove(final K key) {
        try {
            this.getCache().remove(key);
            // Ehcache remove 无返回值，返回 Key + Null 表示操作完成
            return Future.succeededFuture(Kv.create(key, null));
        } catch (final Exception e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Future<V> find(final K key) {
        try {
            final V value = this.getCache().get(key);
            // 即使 value 为 null (miss)，Future 也应当是 succeeded (result 为 null)
            return Future.succeededFuture(value);
        } catch (final Exception e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Future<Boolean> clear() {
        try {
            this.getCache().clear();
            return Future.succeededFuture(Boolean.TRUE);
        } catch (final Exception e) {
            return Future.failedFuture(e);
        }
    }

    @Override
    public Future<Set<K>> keySet() {
        try {
            // Ehcache 3.x 遍历性能较低，但在 Memory Cache 模式下通常可接受
            final Set<K> keys = new HashSet<>();
            for (final Cache.Entry<K, V> entry : this.getCache()) {
                keys.add(entry.getKey());
            }
            return Future.succeededFuture(keys);
        } catch (final Exception e) {
            // 发生异常返回空集合比报错更安全
            log.warn("{} keySet 遍历失败: {}", CachedConstant.K_PREFIX_CACHE, e.getMessage());
            return Future.succeededFuture(Collections.emptySet());
        }
    }

    @Override
    public Future<Integer> size() {
        // 复用 keySet 计算大小
        return this.keySet().map(Set::size);
    }
}