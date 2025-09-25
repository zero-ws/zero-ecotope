package io.zerows.core.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2023-06-19
 */
class _Element extends _ED {
    
    /*
     * Array or List calculation
     * 1) elementAdd
     * 2) elementSave
     * 3) elementClimb
     * 4) elementFind
     * 5) elementZip
     * 6) elementGroup
     * 7) elementSubset
     * 8) elementChild
     * 9) elementEach
     * 10) elementFlat
     * 11) elementCompress
     * 12) elementSet
     * 13) elementRevert
     * 14) elementCount
     */

    public static JsonArray elementClimb(final JsonArray children, final JsonArray tree) {
        return Collection.climb(children, tree, null);
    }

    public static JsonArray elementClimb(final JsonObject child, final JsonArray tree) {
        return Collection.climb(child, tree, null);
    }

    public static JsonArray elementChild(final JsonArray children, final JsonArray tree) {
        return Collection.child(children, tree, null);
    }

    public static JsonArray elementChild(final JsonObject child, final JsonArray tree) {
        return Collection.child(child, tree, null);
    }

    public static JsonArray elementZip(final JsonArray array, final String fieldKey,
                                       final String fieldOn, final ConcurrentMap<String, JsonArray> grouped) {
        return Jackson.zip(array, fieldKey, fieldOn, grouped, null);
    }

    public static JsonArray elementZip(final JsonArray array, final String fieldKey,
                                       final String fieldOn,
                                       final ConcurrentMap<String, JsonArray> grouped, final String fieldTo) {
        return Jackson.zip(array, fieldKey, fieldOn, grouped, fieldTo);
    }

    public static <T, V> Set<V> elementSet(final List<T> listT, final Function<T, V> executor) {
        return Collection.set(listT, executor);
    }
}
