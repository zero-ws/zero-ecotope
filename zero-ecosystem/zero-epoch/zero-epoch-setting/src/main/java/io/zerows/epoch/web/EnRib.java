package io.zerows.epoch.web;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KWeb;

class EnRib {

    static <T> JsonObject input(final T data) {
        return EnRibData.input(data);
    }

    static <T> T deserialize(final Object value, final Class<?> clazz) {
        return EnRibData.deserialize(value, clazz);
    }

    static JsonObject outJson(final JsonObject data, final WebException error) {
        return EnRibData.outJson(data, error);
    }

    static Buffer outBuffer(final JsonObject data, final WebException error) {
        return EnRibData.outBuffer(data, error);
    }

    static JsonObject getBody(final JsonObject data) {
        return EnRibData.getBody(data);
    }

    static <T> T get(final JsonObject data) {
        return EnRibData.get(data);
    }

    static <T> T get(final JsonObject data, final String field) {
        return EnRibData.get(data, field);
    }

    static <T> T get(final JsonObject data, final Class<?> clazz) {
        return EnRibData.get(data, clazz);
    }

    static <T> T get(final JsonObject data, final Class<?> clazz, final Integer index) {
        return EnRibData.get(data, clazz, index);
    }

    static <T> void set(final JsonObject data, final String field, final T value, final Integer argIndex) {
        EnRibData.set(data, field, value, argIndex);
    }

    static boolean isIndex(final Integer argIndex) {
        return KWeb.MULTI.INDEXES.containsKey(argIndex);
    }
}
