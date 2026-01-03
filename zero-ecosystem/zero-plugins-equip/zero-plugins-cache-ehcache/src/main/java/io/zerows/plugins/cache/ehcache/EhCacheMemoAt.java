package io.zerows.plugins.cache.ehcache;

import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtBase;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.Set;

public class EhCacheMemoAt<K, V> extends MemoAtBase<K, V> {
    /**
     * 参考 {@link MemoAtBase} 中的注释，数据结构和设计结构是一致的
     *
     * @param vertxRef 容器引用器
     * @param options  缓存配置项
     */
    protected EhCacheMemoAt(final Vertx vertxRef, final MemoOptions<K, V> options) {
        super(vertxRef, options);
    }

    @Override
    public Future<Kv<K, V>> put(final K key, final V value) {
        return null;
    }

    @Override
    public Future<Kv<K, V>> remove(final K key) {
        return null;
    }

    @Override
    public Future<V> find(final K key) {
        return null;
    }

    @Override
    public Future<Boolean> clear() {
        return null;
    }

    @Override
    public Future<Set<K>> keySet() {
        return null;
    }

    @Override
    public Future<Integer> size() {
        return null;
    }
}
