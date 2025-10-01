package io.zerows.core.web.cache;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.User;
import io.zerows.epoch.exception.web._60050Exception501NotSupport;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@SuppressWarnings("all")
public interface Rapid<K, V> {
    /*
     * Pool:
     *    k = t
     */
    static <TK, TV> Rapid<TK, TV> object(final String key) {
        return object(key, -1);
    }

    static <TK, TV> Rapid<TK, TV> object(final String key, final int ttl) {
        return P.CC_RAPID.pick(() -> new RapidObject<TV>(key, ttl),
            RapidObject.class.getName() + key + ttl);
        // return FnZero.po?lThread(P.CACHED_THREAD, () -> new RapidObject<TV>(key, ttl), RapidObject.class.getName() + key + ttl);
    }

    /*
     * Pool:
     *    k1 = t1
     *    k2 = t2
     */
    static Rapid<Set<String>, ConcurrentMap<String, JsonArray>> map(final String key) {
        return map(key, -1);
    }

    static Rapid<Set<String>, ConcurrentMap<String, JsonArray>> map(final String key, final int ttl) {
        return P.CC_RAPID.pick(() -> new RapidDict(key, ttl),
            RapidDict.class.getName() + key + ttl);
        // return FnZero.po?lThread(P.CACHED_THREAD, () -> new RapidDict(key, ttl), RapidDict.class.getName() + key + ttl);
    }

    /*
     * Pool:
     *    habitus = ...
     */
    static <T> Rapid<String, T> user(final User user, final String rootKey) {
        return P.CC_RAPID.pick(() -> new RapidUser<T>(user, rootKey),
            RapidUser.class.getName() + String.valueOf(user.hashCode()) + rootKey);
        // return FnZero.po?lThread(P.CACHED_THREAD, () -> new RapidUser<Tool>(user, rootKey), RapidUser.class.getName() + String.valueOf(user.hashCode()) + rootKey);
    }

    static <T> Rapid<String, T> user(final User user) {
        return user(user, null);
    }

    default Future<V> cached(final K key, final Supplier<Future<V>> executor) {
        throw new _60050Exception501NotSupport(getClass());
    }

    default Future<V> cached(final K key, final Function<K, Future<V>> executor) {
        throw new _60050Exception501NotSupport(getClass());
    }

    Future<V> write(K key, V value);

    Future<V> writeMulti(Set<K> keySet, V value);

    Future<V> clear(K key);

    Future<Boolean> writeMulti(Set<K> keySet);

    Future<V> read(K key);
}

@SuppressWarnings("all")
interface P {

    Cc<String, Rapid> CC_RAPID = Cc.openThread();
}