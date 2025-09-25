package io.zerows.core.web.model.commune;

import io.zerows.core.exception.WebException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KWeb;

class Rib {

    static <T> JsonObject input(final T data) {
        return RibTool.input(data);
    }

    static <T> T deserialize(final Object value, final Class<?> clazz) {
        return RibTool.deserialize(value, clazz);
    }

    static JsonObject outJson(final JsonObject data, final WebException error) {
        return RibTool.outJson(data, error);
    }

    static Buffer outBuffer(final JsonObject data, final WebException error) {
        return RibTool.outBuffer(data, error);
    }

    static JsonObject getBody(final JsonObject data) {
        return RibData.getBody(data);
    }

    static <T> T get(final JsonObject data) {
        return RibData.get(data);
    }

    static <T> T get(final JsonObject data, final Class<?> clazz) {
        return RibData.get(data, clazz);
    }

    static <T> T get(final JsonObject data, final Class<?> clazz, final Integer index) {
        return RibData.get(data, clazz, index);
    }

    static <T> void set(final JsonObject data, final String field, final T value, final Integer argIndex) {
        RibData.set(data, field, value, argIndex);
    }

    static boolean isIndex(final Integer argIndex) {
        return KWeb.MULTI.INDEXES.containsKey(argIndex);
    }
}
