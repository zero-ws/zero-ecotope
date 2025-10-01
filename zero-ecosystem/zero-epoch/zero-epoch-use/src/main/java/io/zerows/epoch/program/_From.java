package io.zerows.epoch.program;

import io.vertx.core.json.JsonObject;

/**
 * @author lang : 2023-06-19
 */
class _From extends _Feature {
    /*
     * String conversation
     * 1) fromBuffer
     * 2) fromObject
     * 3) fromJObject
     * 4) fromJoin
     * 5) fromAdjust
     * 6) fromExpression
     * 7) fromExpressionT
     * 8) fromPrefix
     * 9) fromDate
     */

    public static String fromObject(final Object value) {
        return StringUtil.from(value);
    }

    public static String fromJObject(final JsonObject value) {
        return StringUtil.from(value);
    }

    public static JsonObject fromPrefix(final JsonObject data, final String prefix) {
        return StringUtil.prefix(data, prefix);
    }
}
