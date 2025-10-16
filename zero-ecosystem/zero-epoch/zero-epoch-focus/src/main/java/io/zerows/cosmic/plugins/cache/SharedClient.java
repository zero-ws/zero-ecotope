package io.zerows.cosmic.plugins.cache;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.zerows.platform.metadata.Kv;
import io.zerows.sdk.plugins.AddOn;

import java.util.Set;

/**
 * Shared client for shared data in vert.x
 */
@AddOn.Name("DEFAULT_SHARED_CLIENT")
public interface SharedClient {

    String DEFAULT_NAME = "DEFAULT_SHARED_CLIENT";

    /**
     * Create local map from shared data
     */
    static SharedClient createClient(final Vertx vertx, final String name) {
        return SharedClientImpl.create(vertx, name);
    }

    <K, V> Kv<K, V> put(K key, V value);

    <K, V> Kv<K, V> put(K key, V value, int expiredSecs);

    <K, V> Kv<K, V> remove(K key);

    <K, V> V get(K key);

    <K, V> V get(K key, boolean once);

    boolean clear();

    int size();

    <K, V> Set<K> keys();

    @Fluent
    <K, V> SharedClient put(K key, V value, Handler<AsyncResult<Kv<K, V>>> handler);

    @Fluent
    <K, V> SharedClient put(K key, V value, int expiredSecs, Handler<AsyncResult<Kv<K, V>>> handler);

    @Fluent
    <K, V> SharedClient remove(K key, Handler<AsyncResult<Kv<K, V>>> handler);

    @Fluent
    <K, V> SharedClient get(K key, Handler<AsyncResult<V>> handler);

    @Fluent
    <K, V> SharedClient get(K key, boolean once, Handler<AsyncResult<V>> handler);

    @Fluent
    SharedClient clear(Handler<AsyncResult<Boolean>> handler);

    /*
     * Map count for usage
     */
    @Fluent
    SharedClient size(Handler<AsyncResult<Integer>> handler);

    @Fluent
    <K, V> SharedClient keys(Handler<AsyncResult<Set<K>>> handler);
}
