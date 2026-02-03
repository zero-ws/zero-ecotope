package io.zerows.plugins.redis.cache;

import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Response;
import io.zerows.support.Ut;

import java.io.Serializable;

class RedisEnc {
    static final Buffer NULL_BUFFER = Buffer.buffer("__NULL__");

    @SuppressWarnings("unchecked")
    static <K, V> V decode(final Response resp, final RedisYmConfig config, final MemoOptions<K, V> options) {
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
        if ("json".equalsIgnoreCase(config.getFormat())) {
            return Ut.deserialize(buffer.toString(), options.classV());
        }

        // 二进制反序列化
        final Object raw = Json.decodeValue(buffer, options.classV()); // R2MO.deserialize(buffer.getBytes());

        // 检查是否为 JSON 包装器，如果是则还原
        if (raw instanceof RedisEnc.JsonContainer) {
            return (V) ((RedisEnc.JsonContainer) raw).toOriginal();
        }

        // 普通对象直接返回
        return (V) raw;
    }

    static <V> Buffer encode(final V value, final RedisYmConfig config) {
        if (value == null) {
            return null;
        }
        // 兼容 JSON 模式 (纯文本存储)
        if ("json".equalsIgnoreCase(config.getFormat())) {
            return Buffer.buffer(Ut.serialize(value));
        }

        // 二进制序列化
        final Object converted;
        if (value instanceof final JsonObject json) {
            // 包装为可序列化的 Container
            converted = new RedisEnc.JsonContainer(json.encode(), true);
        } else if (value instanceof final JsonArray jarr) {
            // 包装为可序列化的 Container
            converted = new RedisEnc.JsonContainer(jarr.encode(), false);
        } else {
            // 其他实现了 Serializable 的 POJO 或基本类型
            converted = value;
        }
        return Json.encodeToBuffer(converted);
    }

    /**
     * 用于解决 Vert.x JsonObject/JsonArray 无法直接序列化的问题。
     * 将其转为 String 存入，并在取出时根据 flag 还原。
     *
     * @param isObject true = JsonObject, false = JsonArray
     */
    private record JsonContainer(String data, boolean isObject) implements Serializable {

        Object toOriginal() {
            // 还原为 Vert.x 的对象
            return this.isObject ? new JsonObject(this.data) : new JsonArray(this.data);
        }
    }
}
