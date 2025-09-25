package io.zerows.core.web.cache.shared;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.zerows.common.program.Kv;
import io.zerows.core.fn.Fx;
import io.zerows.core.util.Ut;
import io.zerows.core.web.cache.shared.exception._500PoolInternalException;
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
        return Fx.<Kv<K, V>>pack(future -> this.client.put(key, value, res -> {
            LOGGER.debug(INFO.UxPool.POOL_PUT, key, value, this.name);
            Fx.pack(res, future, Ut.failWeb(_500PoolInternalException.class, this.getClass(), this.name, "put"));
        }));
    }

    public <K, V> Future<Kv<K, V>> put(final K key, final V value, int expiredSecs) {
        return Fx.<Kv<K, V>>pack(future -> this.client.<K, V>put(key, value, expiredSecs, res -> {
            LOGGER.debug(INFO.UxPool.POOL_PUT_TIMER, key, value, this.name, String.valueOf(expiredSecs));
            Fx.pack(res, future, Ut.failWeb(_500PoolInternalException.class, this.getClass(), this.name, "put"));
        }));
    }

    // Remove
    public <K, V> Future<Kv<K, V>> remove(final K key) {
        return Fx.<Kv<K, V>>pack(future -> this.client.<K, V>remove(key, res -> {
            LOGGER.debug(INFO.UxPool.POOL_REMOVE, key, this.name);
            Fx.pack(res, future, Ut.failWeb(_500PoolInternalException.class, this.getClass(), this.name, "remove"));
        }));
    }

    // Get
    public <K, V> Future<V> get(final K key) {
        return Fx.<V>pack(future -> this.client.get(key, res -> {
            LOGGER.debug(INFO.UxPool.POOL_GET, key, this.name, false);
            Fx.pack(res, future, Ut.failWeb(_500PoolInternalException.class, this.getClass(), this.name, "get"));
        }));
    }

    public <K, V> Future<ConcurrentMap<K, V>> get(final Set<K> keys) {
        final ConcurrentMap<K, Future<V>> futureMap = new ConcurrentHashMap<>();
        keys.forEach(key -> futureMap.put(key, this.get(key)));
        return Fx.combineM(futureMap);
    }

    public <K, V> Future<V> get(final K key, final boolean once) {
        return Fx.<V>pack(future -> this.client.get(key, once, res -> {
            LOGGER.debug(INFO.UxPool.POOL_GET, key, this.name, once);
            Fx.pack(res, future, Ut.failWeb(_500PoolInternalException.class, this.getClass(), this.name, "get"));
        }));
    }

    public Future<Boolean> clear() {
        return Fx.<Boolean>pack(future -> this.client.clear(res -> {
            LOGGER.debug(INFO.UxPool.POOL_CLEAR, this.name);
            Fx.pack(res, future, Ut.failWeb(_500PoolInternalException.class, this.getClass(), this.name, "clear"));
        }));
    }

    // Count
    public Future<Integer> size() {
        return Fx.<Integer>pack(future -> this.client.size(res -> {
            Fx.pack(res, future, Ut.failWeb(_500PoolInternalException.class, this.getClass(), this.name, "size"));
        }));
    }

    public Future<Set<String>> keys() {
        return Fx.<Set<String>>pack(future -> this.client.keys(res -> {
            Fx.pack(res, future, Ut.failWeb(_500PoolInternalException.class, this.getClass(), this.name, "keys"));
        }));
    }
}
