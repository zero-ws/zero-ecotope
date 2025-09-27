package io.zerows.core.util;

import io.zerows.core.uca.qr.syntax.Ir;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * @author lang : 2023-06-19
 */
@SuppressWarnings("all")
class _Value extends _To {


    /*
     * Mapping operation
     *
     * - valueJObject(JsonObject)                     Null Checking
     * - valueJObject(JsonObject, String)             JsonObject -> field -> JsonObject
     * - valueJArray(JsonArray)                       Null Checking
     * - valueJArray(JsonObject, String)              JsonObject -> field -> JsonArray
     * - valueSetArray(JsonArray, String)             JsonArray -> field -> Set<JsonArray>
     * - valueSetString(JsonArray, String)            JsonArray -> field -> Set<String>
     * - valueSetString(List<Tool>, Function<Tool,String> ) List<Tool> -> function -> Set<String>
     * - valueString(JsonArray, String)               JsonArray -> field -> String ( Unique Mapping )
     * - valueC
     * - valueCI
     *
     * - valueTime(LocalTime, LocalDateTime)
     */

    // Qr Field Processing
    public static String valueQrIn(final String field) {
        return Mapping.vQrField(field, Ir.Op.IN);
    }

    /*
     * 「双态型」两种形态
     */
    public static JsonObject valueToPage(final JsonObject pageData, final String... fields) {
        return To.valueToPage(pageData, fields);
    }

    public static JsonObject valueToPage() {
        return To.valueToPage(new JsonArray(), 0L);
    }

    public static JsonObject valueToPage(final JsonArray data, final long size) {
        return To.valueToPage(data, size);
    }

    public static JsonObject valueToPage(final JsonObject pageData, final Function<JsonArray, JsonArray> function) {
        return To.valueToPage(pageData, function);
    }

    // Single Processing

    // mapping + replace/append
    /*
     * <Flag>Over: Replace the whole input
     * -- reference not change
     * <Flag>: Extract the data from input only
     */
    public static JsonObject valueToOver(final JsonObject input, final JsonObject mapping, final boolean smart) {
        final JsonObject converted = Mapping.vTo(input, mapping, smart);
        input.mergeIn(converted, true);
        return input;
    }

    public static JsonArray valueToOver(final JsonArray input, final JsonObject mapping, final boolean smart) {
        Ut.itJArray(input).forEach(json -> valueToOver(json, mapping, smart));
        return input;
    }

    public static JsonObject valueTo(final JsonObject input, final JsonObject mapping, final boolean smart) {
        return Mapping.vTo(input, mapping, smart);
    }

    public static JsonArray valueTo(final JsonArray input, final JsonObject mapping, final boolean smart) {
        return Mapping.vTo(input, mapping, smart);
    }

    public static JsonArray valueTo(final JsonArray input, final JsonObject mapping, final BinaryOperator<JsonObject> itFn) {
        return Mapping.vTo(input, mapping, true, itFn);
    }

    public static JsonObject valueFromOver(final JsonObject input, final JsonObject mapping, final boolean smart) {
        final JsonObject converted = Mapping.vFrom(input, mapping, smart);
        input.mergeIn(converted, true);
        return input;
    }

    public static JsonArray valueFromOver(final JsonArray input, final JsonObject mapping, final boolean smart) {
        Ut.itJArray(input).forEach(json -> valueFromOver(json, mapping, smart));
        return input;
    }

    public static JsonObject valueFrom(final JsonObject input, final JsonObject mapping, final boolean smart) {
        return Mapping.vFrom(input, mapping, smart);
    }

    public static JsonArray valueFrom(final JsonArray input, final JsonObject mapping, final boolean smart) {
        return Mapping.vFrom(input, mapping, smart);
    }

    public static JsonArray valueFrom(final JsonArray input, final JsonObject mapping, final BinaryOperator<JsonObject> itFn) {
        return Mapping.vFrom(input, mapping, true, itFn);
    }
}
