package io.zerows.plugins.cache;

import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtBase;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * AsyncMap 通用基类，屏蔽 Local 和 Cluster 的差异。
 * <p>
 * 封装了以下通用能力：
 * 1. 懒加载 + 双重检查锁获取 Map 实例。
 * 2. TTL (Time To Live) 自动解析与处理。
 * 3. 数据拷贝策略 (Local 需拷贝防污染，Cluster 天然隔离无需拷贝)。
 *
 * @author lang : 2026-01-02
 */
@Slf4j
public abstract class MemoAtMap<K, V> extends MemoAtBase<K, V> {

    // 数据拷贝器 (Local模式传入深拷贝逻辑，Cluster模式传入 Identity)
    private final Function<V, V> copier;
    // TTL 毫秒数
    private final long ttl;
    // 异步 Map 实例缓存
    private volatile Future<AsyncMap<K, V>> mapFuture;

    /**
     * @param vertx   Vertx 实例
     * @param options 配置项
     * @param copier  拷贝函数
     */
    protected MemoAtMap(final Vertx vertx, final MemoOptions<K, V> options, final Function<V, V> copier) {
        super(vertx, options);
        // 1. 设置拷贝策略，如果未提供则默认不拷贝
        this.copier = Objects.nonNull(copier) ? copier : v -> v;

        // 2. 解析 TTL
        final Duration duration = options.duration();
        if (Objects.nonNull(duration) && !duration.isZero() && !duration.isNegative()) {
            this.ttl = duration.toMillis();
            if (this.ttl > 0) {
                log.info("[ ZERO ] 缓存 [{}] 启用 TTL 机制，过期时间: {} ms", this.name(), this.ttl);
            }
        } else {
            this.ttl = 0;
        }
    }

    /**
     * 子类必须实现此方法来提供具体的 Map 来源
     *
     * @return Future<AsyncMap>
     */
    protected abstract Future<AsyncMap<K, V>> supplyMap();

    /**
     * 获取 Map 单例
     */
    protected Future<AsyncMap<K, V>> getMap() {
        if (this.mapFuture != null) {
            return this.mapFuture;
        }
        synchronized (this) {
            if (this.mapFuture == null) {
                this.mapFuture = this.supplyMap()
                        .onFailure(err -> log.error("[ ZERO ] 获取 AsyncMap 失败，名称: {}", this.name(), err));
            }
        }
        return this.mapFuture;
    }

    /**
     * 内部安全拷贝逻辑
     */
    private V copy(final V val) {
        if (val == null) {
            return null;
        }
        try {
            return this.copier.apply(val);
        } catch (final Exception e) {
            log.warn("[ ZERO ] 数据拷贝失败，降级使用原对象。错误: {}", e.getMessage());
            return val;
        }
    }

    // --- 统一的接口实现 ---

    @Override
    public Future<Kv<K, V>> put(final K key, final V value) {
        // 入库前拷贝 (取决于构造时传入的 copier)
        final V safeValue = this.copy(value);
        return this.getMap().compose(map -> {
            if (this.ttl > 0) {
                return map.put(key, safeValue, this.ttl).map(v -> Kv.create(key, safeValue));
            } else {
                return map.put(key, safeValue).map(v -> Kv.create(key, safeValue));
            }
        });
    }

    @Override
    public Future<Kv<K, V>> remove(final K key) {
        return this.getMap().compose(map ->
                map.remove(key).map(val -> Kv.create(key, val))
        );
    }

    @Override
    public Future<V> find(final K key) {
        return this.getMap().compose(map ->
                // 出库后拷贝
                map.get(key).map(this::copy)
        );
    }

    @Override
    public Future<Boolean> clear() {
        return this.getMap().compose(map ->
                map.clear().map(true)
        );
    }

    @Override
    public Future<Set<K>> keySet() {
        return this.getMap().compose(map ->
                map.keys().map(HashSet::new)
        );
    }

    @Override
    public Future<Integer> size() {
        return this.getMap().compose(map ->
                map.size().map(Integer::valueOf)
        );
    }
}