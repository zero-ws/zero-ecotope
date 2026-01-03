package io.zerows.plugins.cache;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 共享客户端方法，底层 {@link CachedClient} 和 {@link SharedClient} 都实现此接口，但此接口是 package 域的，只是为了统一定义接口而已
 * <pre>
 *     此 Client 和底层不同的点
 *     - 超时设置 {@link Duration} 放在 put 方法中
 *     - get 方法支持 once 参数，获取后立即删除
 * </pre>
 *
 * @author lang : 2026-01-02
 */
interface BaseClient {

    // 缓存具体实现的单例池 (Key: Fingerprint, Value: MemoAt Instance)
    Cc<String, MemoAt<?, ?>> CC_MEMO = Cc.openThread();
    Cc<String, MemoOptions<?, ?>> CC_OPTION = Cc.open();

    /**
     * 核心静态工厂：负责从缓存池中提取或创建缓存实例，对非 EhCache 的缓存实现而言都可以走标准流程，但 EhCache 需要特殊处理，只能通过
     * 子类来控制，而不可直接使用此接口。
     * 注：EhCache 要求在初始化的时候指定所有的参数，如键类型、值类型、尺寸、过期时间等，无法做到“按需创建”，因此只能通过子类来实现。
     * 指纹计算维度：
     * <pre>
     *    - 调用者类 (caller)
     *    - 配置项目类型
     *    - 缓存名称
     *    - 键类型 / 值类型
     * </pre>
     * 指纹计算中的指纹只是面向 MemoAt 组件，由于底层采用了 name 做缓存管理，且每个缓存管理器会直接和 name 挂钩，所以此处的其他维度
     * 并不影响底层缓存的命中，但 EhCache 则要单独考虑，EhCache 底层缓存管理还会依赖其他四个维度，所以此处底层必须使用二级模式来管理
     * <pre>
     *     1. EhCache 构造基于 name 的缓存管理器
     *     2. 基于参数重新构造缓存池，缓存池使用 key = Cache 的方式，此时的 key 则直接考虑所有维度
     * </pre>
     *
     * @param vertxRef 容器引用
     * @param options  配置项
     */
    @SuppressWarnings("unchecked")
    static <K, V> MemoAt<K, V> of(final Vertx vertxRef, final MemoOptions<K, V> options) {
        /*
         * 1. 计算缓存指纹 (唯一 ID)
         */
        final String keyCache = options.fingerprint();
        final Class<?> implCls = options.caller();

        // 2. [新增] 托管 Options
        // 如果池中已有相同指纹的 Options，则直接复用；否则存入当前 options
        // 这一步确保了后续创建 MemoAt 实例时，使用的是“标准化”后的 Options 对象
        final MemoOptions<K, V> managedOptions = (MemoOptions<K, V>) CC_OPTION.pick(() -> options, keyCache);

        // 3. 托管/获取 MemoAt 实例
        // 使用 managedOptions 进行反射构造
        return (MemoAt<K, V>) CC_MEMO.pick(
                () -> SourceReflect.instance(implCls, vertxRef, managedOptions),
                keyCache
        );
    }

    /**
     * 有此组件的实现就可以搞定底层操作
     *
     * @param expiredAt 过期时间
     * @param <K>       键类型
     * @param <V>       值类型
     * @return MemoAt 实例
     */
    <K, V> MemoAt<K, V> memoAt(Duration expiredAt);

    default <K, V> MemoAt<K, V> memoAt() {
        return this.memoAt(Duration.ZERO);
    }

    // ------ 核心逻辑方法（接口实现）

    default <K, V> Future<Kv<K, V>> put(final K key, final V value, final Duration expiredAt) {
        return this.<K, V>memoAt(expiredAt).put(key, value);
    }

    default <K, V> Future<Kv<K, V>> put(final K key, final V value, final int expiredAt) {
        return this.put(key, value, Duration.ofSeconds(expiredAt));
    }

    default <K, V> Future<Kv<K, V>> put(final K key, final V value) {
        return this.put(key, value, Duration.ZERO);
    }

    default <K, V> Future<Kv<K, V>> remove(final K key) {
        return this.<K, V>memoAt().remove(key);
    }

    default <K, V> Future<Boolean> remove(final Set<K> keys) {
        return this.<K, V>memoAt().remove(keys);
    }

    default <K, V> Future<V> get(final K key, final boolean once) {
        if (!once) {
            return this.<K, V>memoAt().find(key);
        }

        return this.<K, V>memoAt().find(key).compose(found -> {
            if (found == null) {
                return Future.succeededFuture(null);
            }
            return this.<K, V>memoAt().remove(key).map(v -> found);
        });
    }

    default <K, V> Future<ConcurrentMap<K, V>> get(final Set<K> keys) {
        return this.<K, V>memoAt().find(keys);
    }

    default <K, V> Future<V> get(final K key) {
        return this.get(key, false);
    }

    default <K, V> Future<Set<K>> keySet() {
        return this.<K, V>memoAt().keySet();
    }

    default <K, V> Future<Boolean> clear() {
        return this.<K, V>memoAt().clear();
    }

    default <K, V> Future<Integer> size() {
        return this.<K, V>memoAt().size();
    }
}
