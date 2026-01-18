package io.zerows.plugins.cache;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jce.common.HED;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 统一接口，替换原始的 UxPool 以及 Rapid，用于处理不同业务场景之下的共享数据池操作，内置 Bridge 桥接功能。
 * <pre>
 *     1. 初始化时选择使用哪种实现 -> {@link SharedClient} / {@link CachedClient}
 *        - {@link SharedClient}
 *          - {@link MemoAtMapLocal} 本地同步缓存
 *          - {@link MemoAtMapCluster} 集群异步缓存
 *        - {@link CachedClient}
 *          - 通过 SPI 抓取实现类
 *     2. 提供统一的缓存操作接口 / 不同场景之下调用的 Client 会有所区别
 *     3. 开发调用
 *        - HMM 可直接调用，模式正确即可
 *        - {@link SharedClient} 使用注入模式，直接根据 vertx 是否启用了集群 isClustered 来判断使用哪种
 *        - {@link CachedClient} 则在注入过程中使用 SPI 机制加载对应的实现（会多一个参数执行选择）
 * </pre>
 * 上层可定义新的 Mem 接口用于存储不同数据结构，其中包括，存储在什么位置取决于 {@link HMM} 初始化参数
 * <pre>
 *     - {@link JsonObject} 基本键值对
 *     - {@link JsonArray}  字典列表
 *     - {@link UserAt}     登录用户缓存信息（安全对接）
 * </pre>
 * 最终架构如：
 * <pre>
 *     - {@link JsonObject} --> HMM --> MemoAt --> Shared / Local
 *     - {@link JsonArray}  -->                    Shared / Cluster
 *     - {@link UserAt}     -->                    Cached / Redis
 *                                                 Cached / Caffeine
 *                                                 Cached / Ehcache
 *     - MemoAt 捆绑了 Name / Duration / Size 核心参数
 * </pre>
 */
@Slf4j
public class HMM<K, V> {

    private static final Cc<String, HMM<?, ?>> CC_MM = Cc.open();
    private static final String HMM_DEFAULT = HMM.class.getName();
    private final String name;
    private final BaseClient client;

    private HMM(final String name, final boolean isNative) {
        this.name = name;
        this.client = isNative ?
            SharedAddOn.of().createInstance(name) :
            CachedAddOn.of().createInstance(name);
    }

    /**
     * 实现方式
     * <pre>
     *     - Redis
     *     - Ehcache
     *     - Caffeine
     * </pre>
     *
     * @param name 缓存名称
     * @return HMM 实例
     */
    @SuppressWarnings("all")
    public static <K, V> HMM<K, V> of(final String name) {
        final String nameHMM = Ut.isNil(name) ? HMM_DEFAULT : name;
        final String nameSHA = HED.encryptSHA256(nameHMM);
        return (HMM<K, V>) CC_MM.pick(() -> new HMM<>(nameSHA, false), nameSHA);
    }

    /**
     * 本地/集群方式
     * <pre>
     *     - Local   本地缓存
     *     - Cluster 集群缓存
     * </pre>
     *
     * @param name 缓存名称
     * @return HMM, 实例
     */
    @SuppressWarnings("all")
    public static <K, V> HMM<K, V> vertx(final String name) {
        final String nameHMM = Ut.isNil(name) ? HMM_DEFAULT : name;
        final String nameSHA = HED.encryptSHA256(nameHMM);
        return (HMM<K, V>) CC_MM.pick(() -> new HMM<>(nameSHA, true), nameSHA);
    }

    public String name() {
        return this.name;
    }

    public Future<V> put(final K key, final V value) {
        return this.put(key, value, Duration.ZERO);
    }

    public Future<V> put(final K key, final V value, final long expiredAt) {
        return this.put(key, value, Duration.ofSeconds(expiredAt));
    }

    public Future<V> put(final K key, final V value, final Duration expiredAt) {
        return this.client.put(key, value, expiredAt)
            .compose(kv -> Future.succeededFuture(kv.value()));
    }

    public Future<V> putMulti(final Set<K> keys, final V value) {
        return this.putMulti(keys, value, Duration.ZERO);
    }

    public Future<V> putMulti(final Set<K> keys, final V value, final long expiredAt) {
        return this.putMulti(keys, value, Duration.ofSeconds(expiredAt));
    }

    public Future<V> putMulti(final Set<K> keys, final V value, final Duration expiredAt) {
        final List<Future<V>> futures = new ArrayList<>();
        for (final K key : keys) {
            futures.add(this.put(key, value, expiredAt));
        }
        return Future.all(futures).compose(nil -> Future.succeededFuture(value));
    }

    public Future<V> remove(final K key) {
        return this.client.<K, V>remove(key)
            .compose(kv -> Future.succeededFuture(Objects.isNull(kv.value()) ? null : kv.value()));
    }

    public Future<Boolean> remove(final Set<K> keys) {
        return this.client.remove(keys);
    }

    public Future<V> find(final K key) {
        return this.client.get(key, false);
    }

    public Future<V> find(final K key, final boolean once) {
        return this.client.get(key, once);
    }

    public Future<ConcurrentMap<K, V>> find(final Set<K> keys) {
        return this.client.get(keys);
    }

    public Future<V> cached(final K key, final Supplier<Future<V>> executor) {
        return this.cached(key, executor, Duration.ZERO);
    }

    public Future<V> cached(final K key, final Supplier<Future<V>> executor, final long seconds) {
        return this.cached(key, executor, Duration.ofSeconds(seconds));
    }

    public Future<V> cached(final K key, final Supplier<Future<V>> executor, final Duration duration) {
        return this.client.<K, V>get(key).compose(queried -> {
            if (Objects.nonNull(queried)) {
                return Future.succeededFuture(queried);
            }
            return executor.get().compose(actual -> {
                if (Objects.isNull(actual)) {
                    return Future.succeededFuture();
                }
                return this.put(key, actual, duration);
            });
        });
    }

    public Future<Set<K>> keySet() {
        return this.client.keySet();
    }

    public Future<Boolean> clear() {
        return this.client.clear();
    }

    public Future<Integer> size() {
        return this.client.size();
    }
}
