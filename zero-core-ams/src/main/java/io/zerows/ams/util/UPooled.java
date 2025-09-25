package io.zerows.ams.util;

import io.zerows.ams.constant.VString;
import io.zerows.core.exception.internal.PoolNullException;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2023/4/27
 */
final class UPooled {
    private UPooled() {
    }

    static <V> V poolThread(final ConcurrentMap<String, V> pool, final Supplier<V> poolFn, final String key) {
        final String threadName = Thread.currentThread().getName();
        final String keyPool;
        if (TIs.isNil(key)) {
            keyPool = threadName;
        } else {
            keyPool = key + VString.SLASH + threadName;
        }
        return pool(pool, keyPool, poolFn);
    }

    static <K, V> V pool(final ConcurrentMap<K, V> pool, final K key, final Supplier<V> poolFn) {
        if (Objects.isNull(pool)) {
            // ERR-10004, 缓存传入 pool 不可为空
            throw new PoolNullException(UPooled.class);
        }
        /*
         * 双重检查，防止并发
         */
        V value = pool.get(key);
        if (Objects.isNull(value)) {
            value = poolFn.get();
            if (Objects.nonNull(value)) {
                pool.put(key, value);
            }
        }
        return value;
        // return pool.computeIfAbsent(key, k -> poolFn.get());
        // Caused by: java.lang.IllegalStateException: Recursive update
        //        return pool.computeIfAbsent(key, k -> poolFn.get());
    }
}
