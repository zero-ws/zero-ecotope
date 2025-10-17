package io.zerows.cosmic.plugins.cache;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.zerows.cosmic.plugins.cache.exception._60035Exception500PoolInternal;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Shared Data for pool usage in utility X
 */
@Slf4j
@SuppressWarnings("all")
public class HPO {

    private static final Cc<String, HPO> CC_UX_POOL = Cc.open();

    private transient final String name;
    private transient final SharedClient client;

    private HPO(final String name) {
        this.name = name;
        if (Ut.isNil(name)) {
            this.client = SharedAddOn.of().createInstance();
        } else {
            this.client = SharedAddOn.of().createInstance(name);
        }
    }

    public static HPO of(final String name) {
        final String nameP = Ut.isNil(name) ? HPO.class.getName() : name;
        return CC_UX_POOL.pick(() -> new HPO(nameP), nameP);
    }

    public String name() {
        return this.name;
    }

    // Put Operation
    public <K, V> Future<Kv<K, V>> put(final K key, final V value) {
        return Fx.pack(future -> this.client.put(key, value, res -> {
            log.debug("[ ZERO ] ( Shared ) key = {}, value = {} 已添加到 {}.", key, value, this.name);
            final WebException error = new _60035Exception500PoolInternal(this.name, "put");
            Fx.pack(res, future, error);
        }));
    }

    public <K, V> Future<Kv<K, V>> put(final K key, final V value, final int expiredSecs) {
        return Fx.pack(future -> this.client.put(key, value, expiredSecs, res -> {
            log.debug("[ ZERO ] ( Shared ) key = {}, value = {} 已添加到 {}, 持续 {} 秒."
                , key, value, this.name, String.valueOf(expiredSecs));
            final WebException error = new _60035Exception500PoolInternal(this.name, "put");
            Fx.pack(res, future, error);
        }));
    }

    // Remove
    public <K, V> Future<Kv<K, V>> remove(final K key) {
        return Fx.pack(future -> this.client.remove(key, res -> {
            log.debug("[ ZERO ] ( Shared ) key = {} 已从 {} 中移除.", key, this.name);
            final WebException error = new _60035Exception500PoolInternal(this.name, "remove");
            Fx.pack(res, future, error);
        }));
    }

    // Get
    public <K, V> Future<V> get(final K key) {
        return Fx.pack(future -> this.client.get(key, res -> {
            log.debug("[ ZERO ] ( Shared ) key = {} 从 {} 中获取, once = false.", key, this.name);
            final WebException error = new _60035Exception500PoolInternal(this.name, "get");
            Fx.pack(res, future, error);
        }));
    }

    public <K, V> Future<ConcurrentMap<K, V>> get(final Set<K> keys) {
        final ConcurrentMap<K, Future<V>> futureMap = new ConcurrentHashMap<>();
        keys.forEach(key -> futureMap.put(key, this.get(key)));
        return Fx.combineM(futureMap);
    }

    public <K, V> Future<V> get(final K key, final boolean once) {
        return Fx.pack(future -> this.client.get(key, once, res -> {
            log.debug("[ ZERO ] ( Shared ) key = {} 从 {} 中获取, 是否一次性 = {}.", key, this.name, once);
            final WebException error = new _60035Exception500PoolInternal(this.name, "get");
            Fx.pack(res, future, error);
        }));
    }

    public Future<Boolean> clear() {
        return Fx.pack(future -> this.client.clear(res -> {
            log.debug("[ ZERO ] ( Shared ) 所有数据已从 {} 中清除.", this.name);
            final WebException error = new _60035Exception500PoolInternal(this.name, "clear");
            Fx.pack(res, future, error);
        }));
    }

    // Count
    public Future<Integer> size() {
        return Fx.pack(future -> this.client.size(res -> {
            final WebException error = new _60035Exception500PoolInternal(this.name, "size");
            Fx.pack(res, future, error);
        }));
    }

    public Future<Set<String>> keys() {
        return Fx.pack(future -> this.client.keys(res -> {
            final WebException error = new _60035Exception500PoolInternal(this.name, "keys");
            Fx.pack(res, future, error);
        }));
    }
}
