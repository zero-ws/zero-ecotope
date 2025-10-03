package io.zerows.program;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;
import io.zerows.support.Ut;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author lang : 2023-06-11
 */
class _Where extends _Update {
    /*
     * JqTool Engine method
     * 1) whereDay
     * 2) whereAnd
     * 3) whereOr
     * 4) whereKeys
     */
    public static JsonObject whereDay(final JsonObject filters, final String field, final Instant instant) {
        return QrWhere.whereDay(filters, field, instant);
    }

    public static JsonObject whereDay(final JsonObject filters, final String field, final LocalDateTime instant) {
        return QrWhere.whereDay(filters, field, Ut.parse(instant).toInstant());
    }

    public static JsonObject whereKeys(final Set<String> keys) {
        return QrWhere.whereKeys(Ut.toJArray(keys));
    }

    public static JsonObject whereKeys(final JsonArray keys) {
        return QrWhere.whereKeys(keys);
    }

    public static JsonObject whereAnd() {
        return QrWhere.whereAnd();
    }

    public static JsonObject whereAnd(final String field, final Object value) {
        return QrWhere.whereAnd().put(field, value);
    }

    public static JsonObject whereOr() {
        return QrWhere.whereOr();
    }

    public static JsonObject whereOr(final String field, final Object value) {
        return QrWhere.whereOr().put(field, value);
    }

    public static JsonObject whereAmb(final ClusterSerializable json, final String field) {
        return QrWhere.whereAmb(json, field, field, true);
    }

    public static JsonObject whereAmb(final ClusterSerializable json,
                                      final String fieldFrom, final String fieldTo) {
        return QrWhere.whereAmb(json, fieldFrom, fieldTo, true);
    }
}
