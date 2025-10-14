package io.zerows.cosmic.plugins.cache;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.zerows.cosmic.plugins.cache.exception._60034Exception500SharedDataMode;
import io.zerows.epoch.annotations.Defer;
import io.zerows.platform.metadata.Kv;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

@SuppressWarnings("all")
@Defer
@Slf4j
public class SharedClientImpl implements SharedClient {

    private static final Cc<String, SharedClient> CC_CLIENTS = Cc.open();

    private final transient Vertx vertx;
    private transient String poolName;

    SharedClientImpl(final Vertx vertx, final String name) {
        this.vertx = vertx;
        this.poolName = name;
    }

    public static SharedClient create(final Vertx vertx, final String name) {
        return CC_CLIENTS.pick(() -> new SharedClientImpl(vertx, name), name);
    }

    private <K, V> void async(final Handler<AsyncResult<AsyncMap<K, V>>> handler) {
        final SharedData sd = this.vertx.sharedData();
        // Async map created
        log.info("[ ZERO ] ( Async ) 你正在使用异步模式创建 AsyncMap，开始单例化.");
        sd.<K, V>getAsyncMap(this.poolName).onComplete(res -> {
            if (res.succeeded()) {
                log.info("[ ZERO ] ( Async ) AsyncMap 初始化完成，{} = {}.",
                    this.poolName, String.valueOf(res.result().hashCode()));
                handler.handle(Future.succeededFuture(res.result()));
            } else {
                final WebException error = new _60034Exception500SharedDataMode(res.cause());
                handler.handle(Future.failedFuture(error));
            }
        });
    }

    private <K, V> LocalMap<K, V> sync() {
        final SharedData sd = this.vertx.sharedData();
        // Sync map created
        final LocalMap<K, V> localMap = sd.getLocalMap(this.poolName);
        log.info("[ ZERO ] ( Sync ) 你正在使用同步模式创建 LocalMap, {} = {}.",
            this.poolName, String.valueOf(localMap.hashCode()));
        return localMap;
    }

    @Override
    public <K, V> Kv<K, V> put(final K key, final V value) {
        final V reference = this.<K, V>sync().get(key);
        // Add & Replace
        if (Objects.isNull(reference)) {
            this.sync().put(key, value);
        } else {
            this.sync().replace(key, value);
        }
        return Kv.create(key, value);
    }

    @Override
    public <K, V> Kv<K, V> put(final K key, final V value, final int seconds) {
        Kv<K, V> result = this.put(key, value);
        log.info("[ ZERO ] ( Timer ) {} = {} 已添加到 LocalMap, 持续 {} 秒.", key, value, String.valueOf(seconds));
        this.vertx.setTimer(seconds * 1000, id -> {
            final V existing = this.get(key);
            if (Objects.nonNull(existing)) {
                log.info("[ ZERO ] ( Timer ) LocalMap, key = {} 已过期，数据已移除.", key);
                this.remove(key);
            } else {
                log.info("[ ZERO ] ( Timer ) LocalMap, key = {} 已被移除.", key);
            }
        });
        return result;
    }

    @Override
    public <K, V> SharedClient put(final K key, final V value,
                                   final Handler<AsyncResult<Kv<K, V>>> handler) {
        this.<K, V>async(map -> map.result().get(key).onComplete(res -> {
            if (res.succeeded()) {
                final V reference = res.result();
                if (Objects.isNull(reference)) {
                    map.result()
                        .put(key, value).onComplete(added -> this.putHandler(added, key, value, handler));
                } else {
                    map.result()
                        .replace(key, value).onComplete(replaced -> this.putHandler(replaced, key, value, handler));
                }
            } else {
                final WebException error = new _60034Exception500SharedDataMode(res.cause());
                handler.handle(Future.failedFuture(error));
            }
        }));
        return this;
    }

    @Override
    public <K, V> SharedClient put(final K key, final V value, final int seconds,
                                   final Handler<AsyncResult<Kv<K, V>>> handler) {
        log.info("[ ZERO ] ( Timer ) {} = {} 已添加到 AsyncMap, 持续 {} 秒.", key, value, String.valueOf(seconds));
        final Integer ms = seconds * 1000;
        this.<K, V>async(map -> map.result().get(key).onComplete(res -> {
            if (res.succeeded()) {
                final V reference = res.result();
                if (Objects.isNull(reference)) {
                    map.result()
                        .put(key, value, ms).onComplete(added -> this.putHandler(added, key, value, handler));
                } else {
                    map.result()
                        .replace(key, value, ms).onComplete(replaced -> this.putHandler(replaced, key, value, handler));
                }
            } else {
                final WebException error = new _60034Exception500SharedDataMode(res.cause());
                handler.handle(Future.failedFuture(error));
            }
        }));
        return this;
    }

    private <K, V> void putHandler(final AsyncResult done, final K key, final V value,
                                   final Handler<AsyncResult<Kv<K, V>>> handler) {
        if (done.succeeded()) {
            log.info("[ ZERO ] ( Timer ) AsyncMap, key = {} 已过期，数据已移除.", key);
            handler.handle(Future.succeededFuture(Kv.create(key, value)));
        } else {
            final WebException error = new _60034Exception500SharedDataMode(done.cause());
            handler.handle(Future.failedFuture(error));
        }
    }

    @Override
    public <K, V> Kv<K, V> remove(final K key) {
        final V removed = this.<K, V>sync().remove(key);
        return Kv.create(key, removed);
    }

    @Override
    public <K, V> V get(final K key) {
        return this.<K, V>sync().get(key);
    }

    @Override
    public boolean clear() {
        this.sync().clear();
        return true;
    }

    @Override
    public <K, V> V get(final K key, final boolean once) {
        final V value = this.get(key);
        if (once) {
            this.remove(key);
        }
        return value;
    }

    @Override
    public <K, V> SharedClient remove(final K key,
                                      final Handler<AsyncResult<Kv<K, V>>> handler) {
        this.<K, V>async(map -> map.result().remove(key).onComplete(res -> {
            if (res.succeeded()) {
                final V reference = res.result();
                handler.handle(Future.succeededFuture(Kv.create(key, reference)));
            } else {
                final WebException error = new _60034Exception500SharedDataMode(res.cause());
                handler.handle(Future.failedFuture(error));
            }
        }));
        return this;
    }

    @Override
    public <K, V> SharedClient get(final K key,
                                   final Handler<AsyncResult<V>> handler) {
        this.<K, V>async(map -> map.result().get(key).onComplete(handler));
        return this;
    }

    @Override
    public <K, V> SharedClient get(K key, boolean once,
                                   Handler<AsyncResult<V>> handler) {
        final SharedClient reference = this.get(key, handler);
        if (once) {
            this.<K, V>async(map -> map.result().remove(key).onComplete(handler));
        }
        return reference;
    }

    @Override
    public SharedClient clear(Handler<AsyncResult<Boolean>> handler) {
        this.async(map -> map.result().clear().onComplete(result -> handler.handle(Future.succeededFuture(Boolean.TRUE))));
        return this;
    }

    /*
     * Shared Enhancement for
     *
     * 1) Session Management
     * 2) Cache Management
     * 3) Login/Logout Management
     */
    @Override
    public SharedClient size(Handler<AsyncResult<Integer>> handler) {
        this.async(map -> map.result().size().onComplete(handler));
        return this;
    }

    @Override
    public <K, V> SharedClient keys(Handler<AsyncResult<Set<K>>> handler) {
        this.<K, V>async(map -> map.result().keys().onComplete(handler));
        return this;
    }

    @Override
    public int size() {
        return this.sync().size();
    }

    @Override
    public <K, V> Set<K> keys() {
        return this.<K, V>sync().keySet();
    }
}
