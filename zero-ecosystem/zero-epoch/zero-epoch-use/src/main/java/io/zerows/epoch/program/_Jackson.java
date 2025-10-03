package io.zerows.epoch.program;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;

import java.util.List;

/**
 * @author lang : 2023-06-19
 */
@SuppressWarnings("all")
class _Jackson extends _It {

    /*
     * Serialization method operation method here.
     * 1) serialize / serializeJson
     * 2) deserialize
     *
     * Object converting here of serialization
     */
    public static <T, R extends Iterable> R serializeJson(final T t) {
        return Jackson.serializeJson(t, false);
    }

    public static <T> T deserialize(final JsonObject value, final Class<T> type) {
        return Jackson.deserialize(value, type, true);
    }

    public static <T, R extends Iterable> R serializeJson(final T t, final boolean isSmart) {
        return Jackson.serializeJson(t, isSmart);
    }

    public static <T> T deserialize(final JsonObject value, final Class<T> type, boolean isSmart) {
        return Jackson.deserialize(value, type, isSmart);
    }

    public static <T> T deserialize(final JsonArray value, final Class<T> type, boolean isSmart) {
        return Jackson.deserialize(value, type, isSmart);
    }

    public static <T> T deserialize(final String value, final Class<T> clazz, boolean isSmart) {
        return Jackson.deserialize(value, clazz, isSmart);
    }

    /*
     * Mirror 镜像序列化（更标准的序列化接口）
     * fromJson: JsonObject / JsonArray -> Tool
     * toJObject / toJArray: Tool -> JsonObject / JsonArray
     */
    public static <T> T fromJson(final JsonObject data, final Class<T> clazz) {
        return Json.from(data, clazz, "");
    }

    public static <T> List<T> fromJson(final JsonArray array, final Class<T> clazz) {
        return Json.from(array, clazz, "");
    }

    public static <T> List<T> fromPage(final JsonObject data, final Class<T> clazz) {
        final JsonArray pageData = Ut.valueJArray(data.getJsonArray(KName.LIST));
        return fromJson(pageData, clazz);
    }

    public static <T> T fromJson(final JsonObject data, final Class<T> clazz, final String pojo) {
        return Json.from(data, clazz, pojo);
    }

    public static <T> List<T> fromJson(final JsonArray array, final Class<T> clazz, final String pojo) {
        return Json.from(array, clazz, pojo);
    }

    public static <T> JsonObject toJson(final T entity) {
        return Json.toJObject(entity, "");
    }

    public static <T> JsonObject toJson(final T entity, final String pojo) {
        return Json.toJObject(entity, pojo);
    }

    public static <T> JsonArray toJson(final List<T> list) {
        return Json.toJArray(list, "");
    }

    public static <T> JsonArray toJson(final List<T> list, final String pojo) {
        return Json.toJArray(list, pojo);
    }
}
