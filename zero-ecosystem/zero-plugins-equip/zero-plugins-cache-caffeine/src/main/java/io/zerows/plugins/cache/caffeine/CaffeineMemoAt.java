package io.zerows.plugins.cache.caffeine;

import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtBase;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class CaffeineMemoAt<K, V> extends MemoAtBase<K, V> {
    protected CaffeineMemoAt(final Vertx vertxRef, final MemoOptions<K, V> options) {
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
