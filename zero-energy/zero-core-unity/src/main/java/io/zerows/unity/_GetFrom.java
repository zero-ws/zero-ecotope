package io.zerows.unity;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;

import java.util.List;

/**
 * @author lang : 2023-06-11
 */
class _GetFrom extends _Get {

    public static <T> T fromJson(final JsonObject data, final Class<T> clazz) {
        return Ut.fromJson(data, clazz);
    }

    public static <T> List<T> fromJson(final JsonArray array, final Class<T> clazz) {
        return Ut.fromJson(array, clazz);
    }

    public static <T> T fromJson(final JsonObject data, final Class<T> clazz, final String pojo) {
        return Ut.fromJson(data, clazz, pojo);
    }

    public static <T> List<T> fromJson(final JsonArray array, final Class<T> clazz, final String pojo) {
        return Ut.fromJson(array, clazz, pojo);
    }
}
