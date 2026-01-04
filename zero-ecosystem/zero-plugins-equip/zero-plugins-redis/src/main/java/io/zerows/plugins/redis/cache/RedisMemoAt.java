package io.zerows.plugins.redis.cache;

import io.r2mo.base.util.R2MO;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.common.cache.MemoAtBase;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import io.zerows.plugins.redis.RedisActor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Redis 缓存实现（规范化版本）
 * <p>
 * 特性：
 * 1. 默认使用 Java 二进制序列化 (Buffer)。
 * 2. 依赖 R2MO 进行编解码（内部已处理异常）。
 * 3. 修正 TTL 逻辑：0 表示永不过期 (SET)，大于 0 使用过期时间 (SETEX)。
 * </p>
 */
@Slf4j
public class RedisMemoAt<K, V> extends MemoAtBase<K, V> {

    private static final Redis REDIS = RedisActor.ofClient();
    private static final Buffer NULL_BUFFER = Buffer.buffer("__NULL__");
    private static final int BATCH_SIZE = 1000;

    private final RedisYmConfig config;

    protected RedisMemoAt(final Vertx vertxRef, final MemoOptions<K, V> options) {
        super(vertxRef, options);
        Objects.requireNonNull(REDIS, "[ PLUG ] ( Redis ) 客户端未初始化，无法使用 Redis 作为缓存，请检查 Redis 配置！");
        this.config = options.configuration() != null ? options.configuration() : new RedisYmConfig();
    }

    // ---------------- 私有辅助方法：Codec ----------------

    /**
     * 序列化策略
     */
    private Buffer encode(final V value) {
        if (value == null) {
            return null;
        }
        // 兼容 JSON 模式
        if ("json".equalsIgnoreCase(this.config.getFormat())) {
            return Buffer.buffer(Ut.serialize(value));
        }

        // 二进制序列化 (R2MO 内部自带 try-catch)
        final byte[] bytes = R2MO.serialize(value);

        // 🛡️ 防御：如果 R2MO 内部失败返回 null，手动抛出异常终止流程
        if (bytes == null) {
            throw new RuntimeException("[ PLUG ] ( Redis ) R2MO 序列化失败，返回结果为空");
        }
        return Buffer.buffer(bytes);
    }

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

        // 二进制反序列化 (R2MO 内部自带 try-catch)
        // 如果出错返回 null，逻辑上视为 Cache Miss，无需额外处理
        return (V) R2MO.deserialize(buffer.getBytes());
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

    // ---------------- 接口实现 ----------------

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

        // 场景 1: 缓存空值
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

        // 场景 2: 正常缓存
        try {
            // 这里调用 encode，如果 R2MO 返回 null 会抛出 RuntimeException 被这里捕获
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

    @Override
    public Future<V> find(final K key) {
        final String redisKey = this.wrapKey(key);
        return Objects.requireNonNull(REDIS).send(Request.cmd(Command.GET).arg(redisKey))
            .onFailure(t -> log.error("[ PLUG ] ( Redis ) 读取缓存失败: Key={}, Error={}", redisKey, t.getMessage()))
            .map(this::decode);
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
}