package io.zerows.support;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.constant.VString;
import io.zerows.platform.metadata.KMap;

import java.util.Objects;
import java.util.function.BinaryOperator;

/*
 * Mapping data to List / JsonArray here
 * [],[],[],[]
 * Mapped by field here for different usage
 */
class Mapping {

    static String vQrField(final String field, final String strOp) {
        return Ut.isNil(strOp) ? field : field + VString.COMMA + strOp;
    }

    // ------------------------------ 带映射专用方法 ----------------------------
    /*
     * mapping: from = to
     * 1. bMapping.keys() -> froms
     * 2. bMapping.to(String) -> from -> to
     */
    static JsonObject vTo(final JsonObject input, final KMap.Node mapping) {
        final JsonObject outputJ = new JsonObject();
        if (Objects.isNull(input)) {
            return outputJ;
        }
        mapping.keys().forEach(from -> {
            final Object value = input.getValue(from);
            final String to = mapping.to(from);
            outputJ.put(to, value);
        });
        return outputJ;
    }

    static JsonArray vTo(final JsonArray input, final KMap.Node mapping) {
        final JsonArray outputA = new JsonArray();
        Ut.itJArray(input).forEach(json -> outputA.add(vTo(json, mapping)));
        return outputA;
    }

    static JsonObject vTo(final JsonObject input, final JsonObject mapping, final boolean smart) {
        final KMap.Node node = bMapping(mapping, smart);
        return vTo(input, node);
    }

    static JsonArray vTo(final JsonArray input, final JsonObject mapping, final boolean smart) {
        return vTo(input, mapping, smart, null);
    }

    static JsonArray vTo(final JsonArray input, final JsonObject mapping, final boolean smart,
                         final BinaryOperator<JsonObject> itFn) {
        final KMap.Node node = bMapping(mapping, smart);
        final JsonArray outputA = new JsonArray();
        Ut.itJArray(input).forEach(json -> {
            final JsonObject convert = vTo(json, node);
            if (Objects.isNull(itFn)) {
                outputA.add(convert);
            } else {
                outputA.add(itFn.apply(convert, json));
            }
        });
        return outputA;
    }

    static JsonObject vFrom(final JsonObject input, final JsonObject mapping, final boolean smart) {
        final KMap.Node node = bMapping(mapping, smart);
        return vFrom(input, node);
    }

    static JsonArray vFrom(final JsonArray input, final JsonObject mapping, final boolean smart) {
        return vFrom(input, mapping, smart, null);
    }

    static JsonArray vFrom(final JsonArray input, final JsonObject mapping, final boolean smart,
                           final BinaryOperator<JsonObject> itFn) {
        final KMap.Node node = bMapping(mapping, smart);
        final JsonArray outputA = new JsonArray();
        Ut.itJArray(input).forEach(json -> {
            final JsonObject convert = vFrom(json, node);
            if (Objects.isNull(itFn)) {
                outputA.add(convert);
            } else {
                outputA.add(itFn.apply(convert, json));
            }
        });
        return outputA;
    }

    private static JsonObject vFrom(final JsonObject input, final KMap.Node mapping) {
        final JsonObject outputJ = new JsonObject();
        if (Objects.isNull(input)) {
            return outputJ;
        }
        mapping.values().forEach(to -> {
            final Object value = input.getValue(to);
            final String from = mapping.from(to);
            outputJ.put(from, value);
        });
        return outputJ;
    }

    private static JsonArray vFrom(final JsonArray input, final KMap.Node mapping) {
        final JsonArray outputA = new JsonArray();
        Ut.itJArray(input).forEach(json -> outputA.add(vFrom(json, mapping)));
        return outputA;
    }

    private static KMap.Node bMapping(final JsonObject mapping, final boolean smart) {
        final KMap.Node node;
        if (smart) {
            final Object value = mapping.getValue(KName.MAPPING);
            if (value instanceof final JsonObject stored) {
                node = new KMap.Node(stored);
            } else {
                node = new KMap.Node(mapping);
            }
        } else {
            node = new KMap.Node(mapping);
        }
        return node;
    }
}
