package io.zerows.support;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2023-06-19
 */
class _To extends _Math {

    /*
     * To conversation here
     * 1) toJArray
     * 2) toJObject
     * 3) toJValue
     * 4) toMonth / toYear
     * 5) toEnum
     * 6) toCollection / toPrimary
     * 7) toString
     * 8) toDateTime / toDate / toTime / toAdjust
     * 9) toBytes
     * 10) toSet
     * 11) toMap
     * 12) toMatched
     */

    public static Set<String> toMatched(final String input, final String regex) {
        return StringUtil.matched(input, regex);
    }

    public static JsonObject toJObject(final MultiMap map) {
        return To.toJObject(map);
    }


    public static HttpMethod toMethod(final Supplier<String> supplier, final HttpMethod defaultValue) {
        return To.toMethod(supplier, defaultValue);
    }

    public static HttpMethod toMethod(final Supplier<String> supplier) {
        return To.toMethod(supplier, HttpMethod.GET);
    }

    public static HttpMethod toMethod(final String value, final HttpMethod defaultValue) {
        return To.toMethod(() -> value, defaultValue);
    }

    public static HttpMethod toMethod(final String value) {
        return To.toMethod(() -> value, HttpMethod.GET);
    }

    public static <V> ConcurrentMap<String, V> toConcurrentMap(final JsonObject data) {
        return To.toMap(data);
    }

    public static Map<String, Object> toMap(final JsonObject data) {
        return To.toMapExpr(data);
    }
}
