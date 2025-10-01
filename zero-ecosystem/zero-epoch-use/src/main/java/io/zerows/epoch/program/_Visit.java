package io.zerows.epoch.program;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2023-06-19
 */
class _Visit extends _Value {
    /*
     * JsonObject tree visiting
     * 1) visitJObject
     * 2) visitJArray
     * 3) visitInt
     * 4) visitString
     * 5) visitT
     */
    public static JsonObject visitJObject(final JsonObject item, final String... keys) {
        return Jackson.visitJObject(item, keys);
    }

    public static JsonArray visitJArray(final JsonObject item, final String... keys) {
        return Jackson.visitJArray(item, keys);
    }

    public static Integer visitInt(final JsonObject item, final String... keys) {
        return Jackson.visitInt(item, keys);
    }

    public static String visitString(final JsonObject item, final String... keys) {
        return Jackson.visitString(item, keys);
    }

    public static <T> T visitT(final JsonObject item, final String... keys) {
        return Jackson.visitT(item, keys);
    }

    public static <T> T visitTSmart(final JsonObject item, final String path) {
        // Fix issue of split by regex, the . must be \\.
        final String[] pathes = path.split("\\.");
        return Jackson.visitT(item, pathes);
    }
}
