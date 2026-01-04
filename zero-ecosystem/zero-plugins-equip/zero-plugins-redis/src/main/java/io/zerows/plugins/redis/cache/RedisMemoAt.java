package io.zerows.plugins.redis.cache;

import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtBase;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import io.zerows.plugins.redis.RedisActor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class RedisMemoAt<K, V> extends MemoAtBase<K, V> {

    private static final Redis REDIS = RedisActor.ofClient();
    private static final String NULL_PLACEHOLDER = "__NULL__";
    private static final int BATCH_SIZE = 1000;

    private final RedisYmConfig config;

    protected RedisMemoAt(final Vertx vertxRef, final MemoOptions<K, V> options) {
        super(vertxRef, options);
        // 1. 构造时校验 (保留以确保启动即报错)
        Objects.requireNonNull(REDIS, "[ ZERO ] Redis 客户端未初始化，无法使用 Redis 作为缓存，请检查 Redis 配置！");
        // 2. 初始化配置
        this.config = options.configuration() != null ? options.configuration() : new RedisYmConfig();
    }

    // ---------------- 私有辅助方法 ----------------

    private String wrapKey(final K key) {
        if (key == null) {
            return null;
        }
        return this.config.getPrefix() + key;
    }

    @SuppressWarnings("unchecked")
    private K unwrapKey(final String rawKey) {
        final String prefix = this.config.getPrefix();
        if (rawKey == null || !rawKey.startsWith(prefix)) {
            return null;
        }
        final String keyStr = rawKey.substring(prefix.length());
        final Class<K> kClass = this.options().classK();
        if (kClass == null || kClass == String.class) {
            return (K) keyStr;
        }
        return Ut.deserialize(keyStr, kClass);
    }

    // ---------------- 接口实现 ----------------

    @Override
    public Future<Kv<K, V>> put(final K key, final V value) {
        final String redisKey = this.wrapKey(key);
        if (redisKey == null) {
            return Future.failedFuture("Cache key cannot be null");
        }

        if (value == null) {
            if (Boolean.TRUE.equals(this.config.getNullValue())) {
                final long ttl = this.config.nullValueAt().getSeconds();
                final Request req = Request.cmd(Command.SETEX).arg(redisKey).arg(ttl).arg(NULL_PLACEHOLDER);
                // 调用前 check
                return Objects.requireNonNull(REDIS).send(req).map(r -> Kv.create(key, null));
            } else {
                return Future.succeededFuture(Kv.create(key, null));
            }
        }

        long ttl = this.options().duration().getSeconds();
        if (ttl <= 0) {
            ttl = this.config.expiredAt().getSeconds();
        }

        final String serializedValue = Ut.serialize(value);
        final Request req = Request.cmd(Command.SETEX).arg(redisKey).arg(ttl).arg(serializedValue);

        // 调用前 check
        return Objects.requireNonNull(REDIS).send(req).map(r -> Kv.create(key, value));
    }

    @Override
    public Future<Kv<K, V>> remove(final K key) {
        final String redisKey = this.wrapKey(key);
        // 调用前 check
        return Objects.requireNonNull(REDIS).send(Request.cmd(Command.DEL).arg(redisKey))
            .map(resp -> Kv.create(key, null));
    }

    @Override
    public Future<V> find(final K key) {
        final String redisKey = this.wrapKey(key);
        // 调用前 check
        return Objects.requireNonNull(REDIS).send(Request.cmd(Command.GET).arg(redisKey))
            .map(resp -> {
                if (resp == null) {
                    return null;
                }
                final String valStr = resp.toString();
                if (NULL_PLACEHOLDER.equals(valStr)) {
                    return null;
                }
                return Ut.deserialize(valStr, this.options().classV());
            });
    }

    @Override
    public Future<Boolean> clear() {
        return this.scanKeys(this.config.getPrefix() + "*")
            .compose(keys -> {
                if (keys.isEmpty()) {
                    return Future.succeededFuture(Boolean.TRUE);
                }

                final List<String> allKeys = new ArrayList<>(keys);
                // 显式声明泛型 List<Future<?>> 以解决 Vert.x 5 Future.all 的编译错误
                final List<Future<?>> futures = new ArrayList<>();

                // 分批删除
                for (int i = 0; i < allKeys.size(); i += BATCH_SIZE) {
                    final int end = Math.min(allKeys.size(), i + BATCH_SIZE);
                    final List<String> batch = allKeys.subList(i, end);
                    final Request req = Request.cmd(Command.DEL);
                    batch.forEach(req::arg);

                    // 调用前 check
                    futures.add(Objects.requireNonNull(REDIS).send(req));
                }

                return Future.all(futures).map(Boolean.TRUE);
            });
    }

    @Override
    public Future<Set<K>> keySet() {
        return this.scanKeys(this.config.getPrefix() + "*")
            .map(rawKeys -> rawKeys.stream()
                .map(this::unwrapKey)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
    }

    @Override
    public Future<Integer> size() {
        return this.scanKeys(this.config.getPrefix() + "*").map(Set::size);
    }

    // ---------------- 内部 SCAN 实现 ----------------

    private Future<Set<String>> scanKeys(final String pattern) {
        final Promise<Set<String>> promise = Promise.promise();
        final Set<String> result = new HashSet<>();
        this.scanRecursive("0", pattern, result, promise);
        return promise.future();
    }

    private void scanRecursive(final String cursor, final String pattern, final Set<String> accumulator, final Promise<Set<String>> promise) {
        final Request req = Request.cmd(Command.SCAN)
            .arg(cursor)
            .arg("MATCH").arg(pattern)
            .arg("COUNT").arg(1000);

        // 调用前 check
        Objects.requireNonNull(REDIS).send(req).onSuccess(resp -> {
            try {
                final String newCursor = resp.get(0).toString();
                for (final Response item : resp.get(1)) {
                    accumulator.add(item.toString());
                }

                if ("0".equals(newCursor)) {
                    promise.complete(accumulator);
                } else {
                    this.scanRecursive(newCursor, pattern, accumulator, promise);
                }
            } catch (final Exception e) {
                promise.fail(e);
            }
        }).onFailure(promise::fail);
    }
}