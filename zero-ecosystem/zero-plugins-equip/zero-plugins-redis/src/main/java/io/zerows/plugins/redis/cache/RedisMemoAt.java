package io.zerows.plugins.redis.cache;

import io.r2mo.base.util.R2MO;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtBase;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import io.zerows.plugins.redis.RedisActor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis 缓存实现（修复序列化版本）
 */
@Slf4j
public class RedisMemoAt<K, V> extends MemoAtBase<K, V> {

    private static final Redis REDIS = RedisActor.ofClient();
    private static final Buffer NULL_BUFFER = Buffer.buffer("__NULL__");
    private static final int BATCH_SIZE = 1000;

    private final RedisYmConfig config;

    // ---------------- 新增：JSON 包装容器 ----------------

    protected RedisMemoAt(final Vertx vertxRef, final MemoOptions<K, V> options) {
        super(vertxRef, options);
        Objects.requireNonNull(REDIS, "[ PLUG ] ( Redis ) 客户端未初始化，无法使用 Redis 作为缓存，请检查 Redis 配置！");
        this.config = options.configuration() != null ? options.configuration() : new RedisYmConfig();
    }
    // ----------------------------------------------------

    /**
     * 序列化策略
     */
    private Buffer encode(final V value) {
        if (value == null) {
            return null;
        }
        // 兼容 JSON 模式 (纯文本存储)
        if ("json".equalsIgnoreCase(this.config.getFormat())) {
            return Buffer.buffer(Ut.serialize(value));
        }

        // 二进制序列化
        final Object converted;
        if (value instanceof final JsonObject json) {
            // 包装为可序列化的 Container
            converted = new JsonContainer(json.encode(), true);
        } else if (value instanceof final JsonArray jarr) {
            // 包装为可序列化的 Container
            converted = new JsonContainer(jarr.encode(), false);
        } else {
            // 其他实现了 Serializable 的 POJO 或基本类型
            converted = value;
        }

        final byte[] bytes = R2MO.serialize(converted);
        return Buffer.buffer(bytes);
    }

    // ---------------- 私有辅助方法：Codec ----------------

    /**
     * 反序列化策略
     */
    @SuppressWarnings("unchecked")
    private V decode(final Response resp) {
        if (resp == null) {
            return null;
        }
        final Buffer buffer = resp.toBuffer();
        if (buffer == null || buffer.length() == 0) {
            return null;
        }
        if (NULL_BUFFER.equals(buffer)) {
            return null;
        }

        // 兼容 JSON 模式
        if ("json".equalsIgnoreCase(this.config.getFormat())) {
            return Ut.deserialize(buffer.toString(), this.options().classV());
        }

        // 二进制反序列化
        final Object raw = R2MO.deserialize(buffer.getBytes());

        // 检查是否为 JSON 包装器，如果是则还原
        if (raw instanceof JsonContainer) {
            return (V) ((JsonContainer) raw).toOriginal();
        }

        // 普通对象直接返回
        return (V) raw;
    }

    private String wrapKey(final K key) {
        return key == null ? null : this.config.getPrefix() + key;
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

    @Override
    public Future<Kv<K, V>> put(final K key, final V value) {
        final String redisKey = this.wrapKey(key);
        if (redisKey == null) {
            return Future.failedFuture("[ PLUG ] ( Redis ) 缓存 Key 不能为空");
        }

        long ttl = this.options().duration().getSeconds();
        if (ttl <= 0) {
            ttl = this.config.expiredAt().getSeconds();
        }

        if (value == null) {
            if (Boolean.TRUE.equals(this.config.getNullValue())) {
                long nullTtl = this.config.nullValueAt().getSeconds();
                if (nullTtl <= 0) {
                    nullTtl = 60;
                }
                final Request req = Request.cmd(Command.SETEX).arg(redisKey).arg(nullTtl).arg(NULL_BUFFER);
                return Objects.requireNonNull(REDIS).send(req)
                    .onFailure(t -> log.error("[ PLUG ] ( Redis ) 写入空值异常: Key={}, Error={}", redisKey, t.getMessage()))
                    .map(r -> Kv.create(key, null));
            }
            return Future.succeededFuture(Kv.create(key, null));
        }

        try {
            // 调用修复后的 encode
            final Buffer binValue = this.encode(value);
            final Request req;

            if (ttl > 0) {
                req = Request.cmd(Command.SETEX).arg(redisKey).arg(ttl).arg(binValue);
            } else {
                req = Request.cmd(Command.SET).arg(redisKey).arg(binValue);
            }

            return Objects.requireNonNull(REDIS).send(req)
                .onFailure(t -> log.error("[ PLUG ] ( Redis ) 写入缓存异常: Key={}, Error={}", redisKey, t.getMessage()))
                .map(r -> Kv.create(key, value));
        } catch (final Exception e) {
            log.error("[ PLUG ] ( Redis ) 序列化前置检查失败: {}", e.getMessage());
            return Future.failedFuture(e);
        }
    }

    // ---------------- 接口实现 (后续逻辑保持不变) ----------------

    @Override
    public Future<V> find(final K key) {
        final String redisKey = this.wrapKey(key);
        return Objects.requireNonNull(REDIS).send(Request.cmd(Command.GET).arg(redisKey))
            .onFailure(t -> log.error("[ PLUG ] ( Redis ) 读取缓存失败: Key={}, Error={}", redisKey, t.getMessage()))
            .map(this::decode); // 调用修复后的 decode
    }

    @Override
    public Future<Kv<K, V>> remove(final K key) {
        final String redisKey = this.wrapKey(key);
        return Objects.requireNonNull(REDIS).send(Request.cmd(Command.DEL).arg(redisKey))
            .onFailure(t -> log.error("[ PLUG ] ( Redis ) 删除缓存失败: Key={}, Error={}", redisKey, t.getMessage()))
            .map(resp -> Kv.create(key, null));
    }

    @Override
    public Future<Boolean> clear() {
        return this.scanKeys(this.config.getPrefix() + "*")
            .compose(keys -> {
                if (keys.isEmpty()) {
                    return Future.succeededFuture(Boolean.TRUE);
                }

                final List<String> allKeys = new ArrayList<>(keys);
                final List<Future<?>> futures = new ArrayList<>();

                for (int i = 0; i < allKeys.size(); i += BATCH_SIZE) {
                    final int end = Math.min(allKeys.size(), i + BATCH_SIZE);
                    final List<String> batch = allKeys.subList(i, end);
                    final Request req = Request.cmd(Command.DEL);
                    batch.forEach(req::arg);

                    futures.add(Objects.requireNonNull(REDIS).send(req)
                        .onFailure(t -> log.error("[ PLUG ] ( Redis ) 批量清除失败: {}", t.getMessage()))
                    );
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

    private Future<Set<String>> scanKeys(final String pattern) {
        final Promise<Set<String>> promise = Promise.promise();
        this.scanRecursive("0", pattern, new HashSet<>(), promise);
        return promise.future();
    }

    private void scanRecursive(final String cursor, final String pattern, final Set<String> accumulator, final Promise<Set<String>> promise) {
        final Request req = Request.cmd(Command.SCAN).arg(cursor).arg("MATCH").arg(pattern).arg("COUNT").arg(1000);
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
                log.error("[ PLUG ] ( Redis ) SCAN 结果解析错误: {}", e.getMessage());
                promise.fail(e);
            }
        }).onFailure(t -> {
            log.error("[ PLUG ] ( Redis ) SCAN 网络错误: {}", t.getMessage());
            promise.fail(t);
        });
    }

    /**
     * 用于解决 Vert.x JsonObject/JsonArray 无法直接序列化的问题。
     * 将其转为 String 存入，并在取出时根据 flag 还原。
     */
    private static class JsonContainer implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String data;
        private final boolean isObject; // true = JsonObject, false = JsonArray

        JsonContainer(final String data, final boolean isObject) {
            this.data = data;
            this.isObject = isObject;
        }

        Object toOriginal() {
            // 还原为 Vert.x 的对象
            return this.isObject ? new JsonObject(this.data) : new JsonArray(this.data);
        }
    }
}