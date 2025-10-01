package io.zerows.core.web.cache.shared;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.zerows.epoch.common.shared.program.Kv;
import io.zerows.core.fn.FnZero;
import io.zerows.core.util.Ut;
import io.zerows.epoch.web.exception._60035Exception500PoolInternal;
import io.zerows.module.metadata.uca.logging.OLog;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Shared Data for pool usage in utility X
 */
@SuppressWarnings("all")
public class UxPool {

    private static final Cc<String, UxPool> CC_UX_POOL = Cc.open();

    private transient final String name;
    private transient final SharedClient client;
    private transient OLog LOGGER = Ut.Log.ux(getClass());

    private UxPool(final String name) {
        this.name = name;
        this.client = MapInfix.getClient().switchClient(name);
    }

    public static UxPool of(final String name) {
        final String nameP = Ut.isNil(name) ? MapInfix.getDefaultName() : name;
        return CC_UX_POOL.pick(() -> new UxPool(nameP), nameP);
    }

    public String name() {
        return this.name;
    }

    // Put Operation
    public <K, V> Future<Kv<K, V>> put(final K key, final V value) {
        return FnZero.<Kv<K, V>>pack(future -> this.client.put(key, value, res -> {
            LOGGER.debug(INFO.UxPool.POOL_PUT, key, value, this.name);
            final WebException error = new _60035Exception500PoolInternal(this.name, "put");
            FnZero.pack(res, future, error);
        }));
    }

    public <K, V> Future<Kv<K, V>> put(final K key, final V value, int expiredSecs) {
        return FnZero.<Kv<K, V>>pack(future -> this.client.<K, V>put(key, value, expiredSecs, res -> {
            LOGGER.debug(INFO.UxPool.POOL_PUT_TIMER, key, value, this.name, String.valueOf(expiredSecs));
            final WebException error = new _60035Exception500PoolInternal(this.name, "put");
            FnZero.pack(res, future, error);
        }));
    }

    // Remove
    public <K, V> Future<Kv<K, V>> remove(final K key) {
        return FnZero.<Kv<K, V>>pack(future -> this.client.<K, V>remove(key, res -> {
            LOGGER.debug(INFO.UxPool.POOL_REMOVE, key, this.name);
            final WebException error = new _60035Exception500PoolInternal(this.name, "remove");
            FnZero.pack(res, future, error);
        }));
    }

    // Get
    public <K, V> Future<V> get(final K key) {
        return FnZero.<V>pack(future -> this.client.get(key, res -> {
            LOGGER.debug(INFO.UxPool.POOL_GET, key, this.name, false);
            final WebException error = new _60035Exception500PoolInternal(this.name, "get");
            FnZero.pack(res, future, error);
        }));
    }

    public <K, V> Future<ConcurrentMap<K, V>> get(final Set<K> keys) {
        final ConcurrentMap<K, Future<V>> futureMap = new ConcurrentHashMap<>();
        keys.forEach(key -> futureMap.put(key, this.get(key)));
        return FnZero.combineM(futureMap);
    }

    public <K, V> Future<V> get(final K key, final boolean once) {
        return FnZero.<V>pack(future -> this.client.get(key, once, res -> {
            LOGGER.debug(INFO.UxPool.POOL_GET, key, this.name, once);
            final WebException error = new _60035Exception500PoolInternal(this.name, "get");
            FnZero.pack(res, future, error);
        }));
    }

    public Future<Boolean> clear() {
        return FnZero.<Boolean>pack(future -> this.client.clear(res -> {
            LOGGER.debug(INFO.UxPool.POOL_CLEAR, this.name);
            final WebException error = new _60035Exception500PoolInternal(this.name, "clear");
            FnZero.pack(res, future, error);
        }));
    }

    // Count
    public Future<Integer> size() {
        return FnZero.<Integer>pack(future -> this.client.size(res -> {
            final WebException error = new _60035Exception500PoolInternal(this.name, "size");
            FnZero.pack(res, future, error);
        }));
    }

    public Future<Set<String>> keys() {
        return FnZero.<Set<String>>pack(future -> this.client.keys(res -> {
            final WebException error = new _60035Exception500PoolInternal(this.name, "keys");
            FnZero.pack(res, future, error);
        }));
    }
}
