package io.zerows.core.util;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.typed.ChangeFlag;
import io.zerows.epoch.support.UtBase;
import io.zerows.epoch.common.shared.datamation.KMapping;

/**
 * @author lang : 2023-06-19
 */
class _Ai extends UtBase {

    /*
     * Ai analyzing for type based on
     * some different income requirement
     */
    public static Object aiJValue(final Object input, final Class<?> type) {
        return Value.aiJValue(input, type);
    }

    public static Object aiJValue(final Object input) {
        return Value.aiJValue(input, null);
    }

    public static Object aiValue(final Object input, final Class<?> type) {
        return Value.aiValue(input, type);
    }

    public static Object aiValue(final Object input) {
        return Value.aiValue(input, null);
    }

    public static ChangeFlag aiFlag(final JsonObject recordN, final JsonObject recordO) {
        return Jackson.flag(recordN, recordO);
    }

    public static ChangeFlag aiFlag(final JsonObject input) {
        return Jackson.flag(input);
    }

    // Specifical Api for data
    public static JsonObject aiDataN(final JsonObject input) {
        return Jackson.data(input, false);
    }

    public static JsonObject aiDataO(final JsonObject input) {
        return Jackson.data(input, true);
    }

    public static String aiJArray(final String literal) {
        return Jackson.aiJArray(literal);
    }

    public static JsonObject aiIn(final JsonObject in, final KMapping mapping, final boolean keepNil) {
        return Value.aiIn(in, mapping, keepNil);
    }

    public static JsonObject aiIn(final JsonObject in, final KMapping mapping) {
        return Value.aiIn(in, mapping, true);
    }

    public static JsonObject aiOut(final JsonObject out, final KMapping mapping, final boolean keepNil) {
        return Value.aiOut(out, mapping, keepNil);
    }

    public static JsonObject aiOut(final JsonObject out, final KMapping mapping) {
        return Value.aiOut(out, mapping, true);
    }
}
