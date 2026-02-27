package io.zerows.plugins.redis.cache;

import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Response;
import io.zerows.support.Ut;

/**
 * Redis 编码解码器 (极限精简修复版)
 */
class RedisEnc {
    static final Buffer NULL_BUFFER = Buffer.buffer("__NULL__");

    static <K, V> V decode(final Response resp, final RedisYmConfig config, final MemoOptions<K, V> options) {
        if (resp == null) {
            return null;
        }

        final Buffer buffer = resp.toBuffer();
        if (buffer == null || buffer.length() == 0 || NULL_BUFFER.equals(buffer)) {
            return null;
        }

        final String literal = buffer.toString();

        try {
            // 1. 如果是 JsonObject 格式开头，尝试探测包装容器
            if (literal.trim().startsWith("{")) {
                final JsonObject probe = new JsonObject(literal);
                // 仅当包含特定的马甲容器字段时才拆包
                if (probe.containsKey("data") && probe.containsKey("isObject")) {
                    final boolean isObject = Ut.valueT(probe, "isObject", Boolean.class);
                    final String innerData = probe.getString("data");
                    @SuppressWarnings("unchecked") final Class<V> clazz = isObject ? (Class<V>) JsonObject.class : (Class<V>) JsonArray.class;
                    return (V) Ut.deserialize(innerData, clazz);
                }
            }

            // 2. 核心：直接交给 Ut 进行反序列化
            // 只要 options.classV() 是 UserAt.class，Ut 内部的 Jackson 就会寻找
            // 你在 Spring 框架中已经定义好的特定反序列化器。
            return Ut.deserialize(literal, options.classV());

        } catch (final Exception ex) {
            // 如果反序列化失败（比如遇到无法构造抽象类的旧数据），返回 null 触发重刷
            return null;
        }
    }

    static <V> Buffer encode(final V value, final RedisYmConfig config) {
        if (value == null) {
            return null;
        }

        // 1. 基础类型直走序列化
        if (isBasic(value)) {
            return Buffer.buffer(Ut.serialize(value));
        }

        // 2. 针对 Vert.x 原生 JSON 类型的包装逻辑
        if (value instanceof JsonObject) {
            final JsonObject container = new JsonObject();
            container.put("data", ((JsonObject) value).encode());
            container.put("isObject", true);
            return container.toBuffer();
        }

        if (value instanceof JsonArray) {
            final JsonObject container = new JsonObject();
            container.put("data", ((JsonArray) value).encode());
            container.put("isObject", false);
            return container.toBuffer();
        }

        // 3. 业务 POJO (如 UserAt) 的正确处理
        // [修复点]：直接调用 Ut.serialize(value)，让它保留 POJO 本身的序列化特征。
        final JsonObject serialized = Ut.serializeJson(value);
        return serialized.toBuffer();
    }

    private static boolean isBasic(final Object value) {
        return value instanceof String || value instanceof Number ||
            value instanceof Boolean || value instanceof Character ||
            value instanceof Enum;
    }
}