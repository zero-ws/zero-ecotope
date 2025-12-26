package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.support.Ut;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2023-06-11
 */
class _Compare {
    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Function<T, R> fnValue, final String pojoFile) {
        return Ut.compare(original, current, fnValue, pojoFile);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Function<T, R> fnValue) {
        return Ut.compare(original, current, fnValue, VString.EMPTY);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Set<String> uniqueSet, final String pojoFile) {
        return Ut.compare(original, current, uniqueSet, pojoFile);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Set<String> uniqueSet) {
        return Ut.compare(original, current, uniqueSet, VString.EMPTY);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final Set<String> fields) {
        return Ut.compareJ(original, current, fields);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final String field) {
        return Ut.compareJ(original, current, field);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final JsonArray matrix) {
        return Ut.compareJ(original, current, matrix);
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final Set<String> fields) {
        return Ut.compareJAsync(original, current, fields);
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final String field) {
        return Ut.compareJAsync(original, current, field);
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final JsonArray matrix) {
        return Ut.compareJAsync(original, current, matrix);
    }

    public static <T> Future<JsonArray> compareRun(final ConcurrentMap<ChangeFlag, List<T>> compared, final Function<List<T>, Future<List<T>>> insertAsyncFn, final Function<List<T>, Future<List<T>>> updateAsyncFn) {
        return Ut.compareRun(compared, insertAsyncFn, updateAsyncFn);
    }
}
