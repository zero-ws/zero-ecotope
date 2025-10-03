package io.zerows.epoch.corpus;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;

import java.util.List;

/**
 * @author lang : 2023-06-11
 */
class _To extends _Rule {
    /*
     * Entity ( Pojo ) to JsonObject, support pojo file here
     * 1) toJson / fromJson
     * 2) toZip:  Toggle switch from interface style to worker here, the key should be "0", "1", "2", "3", ....
     * 3) toJArray
     * ( Business Part, support `pojoFile` conversation )
     * 4) toFile
     */

    public static <T> JsonObject toJson(final T entity) {
        return Ut.toJson(entity, "");
    }

    public static <T> JsonObject toJson(final T entity, final String pojo) {
        return Ut.toJson(entity, pojo);
    }

    public static <T> JsonArray toJson(final List<T> list) {
        return Ut.toJson(list, "");
    }

    public static <T> JsonArray toJson(final List<T> list, final String pojo) {
        return Ut.toJson(list, pojo);
    }

    public static JsonObject toZip(final Object... args) {
        return ToCommon.toToggle(args);
    }
}
