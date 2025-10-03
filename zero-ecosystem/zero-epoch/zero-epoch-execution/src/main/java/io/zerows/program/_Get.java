package io.zerows.program;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.epoch.metadata.commune.Vis;

/**
 * @author lang : 2023-06-11
 */
class _Get extends _Dict {

    // ---------------------- Request Data Extract --------------------------
    // -> Message<Envelop> -> Tool ( Interface mode )

    public static JsonArray getArray(final Envelop envelop, final int index) {
        return InputRequest.request(envelop, index, JsonArray.class);
    }

    // -> Message<Envelop> -> Tool ( Interface mode )
    public static JsonArray getArray(final Envelop envelop) {
        return InputRequest.request(envelop, 0, JsonArray.class);
    }

    // -> Message<Envelop> -> Tool ( Interface mode )

    public static JsonArray getArray1(final Envelop envelop) {
        return InputRequest.request(envelop, 1, JsonArray.class);
    }

    // -> Message<Envelop> -> Tool ( Interface mode )

    public static JsonArray getArray2(final Envelop envelop) {
        return InputRequest.request(envelop, 2, JsonArray.class);
    }

    // -> Message<Envelop> -> String ( Interface mode )

    public static String getString(final Envelop envelop, final int index) {
        return InputRequest.request(envelop, index, String.class);
    }

    // -> Message<Envelop> -> String ( Interface mode )
    public static String getString(final Envelop envelop) {
        return InputRequest.request(envelop, 0, String.class);
    }

    // -> Message<Envelop> -> String ( Interface mode )
    public static String getString1(final Envelop envelop) {
        return InputRequest.request(envelop, 1, String.class);
    }

    // -> Message<Envelop> -> String ( Interface mode )
    public static String getString2(final Envelop envelop) {
        return InputRequest.request(envelop, 2, String.class);
    }

    // -> Message<Envelop> -> String ( Interface mode )

    public static Vis getVis(final Envelop envelop, final int index) {
        return InputRequest.request(envelop, index, Vis.class);
    }

    // -> Message<Envelop> -> String ( Interface mode )
    public static Vis getVis(final Envelop envelop) {
        return InputRequest.request(envelop, 0, Vis.class);
    }

    // -> Message<Envelop> -> String ( Interface mode )
    public static Vis getVis1(final Envelop envelop) {
        return InputRequest.request(envelop, 1, Vis.class);
    }

    // -> Message<Envelop> -> String ( Interface mode )
    public static Vis getVis2(final Envelop envelop) {
        return InputRequest.request(envelop, 2, Vis.class);
    }

    // -> Message<Envelop> -> JsonObject ( Interface mode )
    public static JsonObject getJson(final Envelop envelop, final int index) {
        return InputRequest.request(envelop, index, JsonObject.class);
    }
    // -> Message<Envelop> -> JsonObject ( Interface mode )

    public static JsonObject getJson(final Envelop envelop) {
        return InputRequest.request(envelop, 0, JsonObject.class);
    }

    // -> Message<Envelop> -> JsonObject ( Interface mode )

    public static JsonObject getJson1(final Envelop envelop) {
        return InputRequest.request(envelop, 1, JsonObject.class);
    }

    // -> Message<Envelop> -> JsonObject ( Interface mode )

    public static JsonObject getJson2(final Envelop envelop) {
        return InputRequest.request(envelop, 2, JsonObject.class);
    }

    // -> Message<Envelop> -> Integer ( Interface mode )

    public static Integer getInteger(final Envelop envelop, final int index) {
        return InputRequest.request(envelop, index, Integer.class);
    }

    public static Integer getInteger(final Envelop envelop) {
        return InputRequest.request(envelop, 0, Integer.class);
    }

    // -> Message<Envelop> -> Integer ( Interface mode )
    public static Integer getInteger1(final Envelop envelop) {
        return InputRequest.request(envelop, 1, Integer.class);
    }

    // -> Message<Envelop> -> Integer ( Interface mode )
    public static Integer getInteger2(final Envelop envelop) {
        return InputRequest.request(envelop, 2, Integer.class);
    }

    // -> Message<Envelop> -> Long ( Interface mode )

    public static Long getLong(final Envelop envelop, final int index) {
        return InputRequest.request(envelop, index, Long.class);
    }

    // -> Message<Envelop> -> Long ( Interface mode )
    public static Long getLong(final Envelop envelop) {
        return InputRequest.request(envelop, 0, Long.class);
    }

    // -> Message<Envelop> -> Long ( Interface mode )
    public static Long getLong1(final Envelop envelop) {
        return InputRequest.request(envelop, 1, Long.class);
    }

    // -> Message<Envelop> -> Long ( Interface mode )
    public static Long getLong2(final Envelop envelop) {
        return InputRequest.request(envelop, 2, Long.class);
    }

    // -> Message<Envelop> -> Tool ( Interface mode )

    public static <T> T getT(final Envelop envelop, final int index, final Class<T> clazz) {
        return InputRequest.request(envelop, index, clazz);
    }

    // -> Message<Envelop> -> Tool ( Interface mode )

    public static <T> T getT(final Envelop envelop, final Class<T> clazz) {
        return InputRequest.request(envelop, 0, clazz);
    }

    // -> Message<Envelop> -> Tool ( Interface mode )

    public static <T> T getT1(final Envelop envelop, final Class<T> clazz) {
        return InputRequest.request(envelop, 1, clazz);
    }

    // -> Message<Envelop> -> Tool ( Interface mode )

    public static <T> T getT2(final Envelop envelop, final Class<T> clazz) {
        return InputRequest.request(envelop, 2, clazz);
    }

    // ---------------------- Agent mode usage --------------------------
    // -> Message<Envelop> -> JsonObject ( Agent mode )
    // -> Envelop -> JsonObject ( Agent mode )
    public static JsonObject getBody(final Envelop envelop) {
        return InputRequest.request(envelop, JsonObject.class);
    }

    // -> Envelop -> Tool ( Agent mode )
    public static <T> T getBodyT(final Envelop envelop, final Class<T> clazz) {
        return InputRequest.request(envelop, clazz);
    }
}
