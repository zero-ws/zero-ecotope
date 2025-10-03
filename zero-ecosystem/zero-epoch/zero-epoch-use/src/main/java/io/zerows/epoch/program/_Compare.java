package io.zerows.epoch.program;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.constant.VString;
import io.zerows.enums.typed.ChangeFlag;
import io.zerows.epoch.based.constant.KName;
import io.zerows.specification.modeling.HRecord;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2024-04-19
 */
class _Compare extends _Bundle {
    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Function<T, R> fnValue, final String pojoFile) {
        return Compare.T.compare(original, current, fnValue, pojoFile);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Function<T, R> fnValue) {
        return Compare.T.compare(original, current, fnValue, VString.EMPTY);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Set<String> uniqueSet, final String pojoFile) {
        return Compare.T.compare(original, current, uniqueSet, pojoFile);
    }

    public static <T, R> ConcurrentMap<ChangeFlag, List<T>> compare(final List<T> original, final List<T> current, final Set<String> uniqueSet) {
        return Compare.T.compare(original, current, uniqueSet, VString.EMPTY);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final Set<String> fields) {
        return Compare.J.compareJ(original, current, fields);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final String field) {
        return Compare.J.compareJ(original, current, field);
    }

    public static ConcurrentMap<ChangeFlag, JsonArray> compareJ(
        final JsonArray original, final JsonArray current, final JsonArray matrix) {
        return Compare.J.compareJ(original, current, matrix);
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final Set<String> fields) {
        return Future.succeededFuture(Compare.J.compareJ(original, current, fields));
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final String field) {
        return Future.succeededFuture(Compare.J.compareJ(original, current, field));
    }

    public static Future<ConcurrentMap<ChangeFlag, JsonArray>> compareJAsync(
        final JsonArray original, final JsonArray current, final JsonArray matrix) {
        return Future.succeededFuture(Compare.J.compareJ(original, current, matrix));
    }

    public static <T> Future<JsonArray> compareRun(final ConcurrentMap<ChangeFlag, List<T>> compared, final Function<List<T>, Future<List<T>>> insertAsyncFn, final Function<List<T>, Future<List<T>>> updateAsyncFn) {
        return Compare.T.run(compared, insertAsyncFn, updateAsyncFn);
    }


    /*
     *  1) ruleJOk
     *  2) ruleJReduce
     *  3) ruleJEqual
     *  4) ruleJFind
     */
    public static boolean ruleJOk(final JsonObject record, final Set<String> fields) {
        return Compare.J.ruleJOk(record, fields);
    }

    public static boolean ruleJOk(final JsonObject record, final JsonArray matrix) {
        return Compare.J.ruleJOk(record, matrix);
    }

    public static JsonArray ruleJReduce(final JsonArray records, final Set<String> fields) {
        return Compare.J.ruleJReduce(records, fields);
    }

    public static JsonArray ruleJReduce(final JsonArray records, final JsonArray matrix) {
        return Compare.J.ruleJReduce(records, matrix);
    }

    public static boolean ruleJEqual(final JsonObject record, final JsonObject latest, final Set<String> fields) {
        return Compare.J.ruleJEqual(record, latest, fields);
    }

    public static boolean ruleJEqual(final JsonObject record, final JsonObject latest, final JsonArray matrix) {
        return Compare.J.ruleJEqual(record, latest, matrix);
    }

    public static JsonObject ruleJFind(final JsonArray source, final JsonObject expected, final Set<String> fields) {
        return Compare.J.ruleJFind(source, expected, fields);
    }

    public static JsonObject ruleJFind(final JsonArray source, final JsonObject expected, final JsonArray matrix) {
        return Compare.J.ruleJFind(source, expected, matrix);
    }

    /*
     * Update Data on Record
     * 1. Generic Tool ( Pojo )
     * 2. List<Tool>
     * 3. JsonObject
     * 4. JsonArray
     * 5. Record
     * 6. Record[]
     */
    public static <T> T cloneT(final T input) {
        return Compare.T.cloneT(input);
    }

    public static <T> T updateT(final T query, final JsonObject params) {
        return Compare.T.updateT(query, params);
    }

    public static <T> List<T> updateT(final List<T> query, final JsonArray params) {
        return Compare.T.updateT(query, params, KName.KEY);
    }

    public static <T> List<T> updateT(final List<T> query, final JsonArray params, final String field) {
        return Compare.T.updateT(query, params, field);
    }

    public static JsonArray updateJ(final JsonArray query, final JsonArray params) {
        return Compare.T.updateJ(query, params, KName.KEY);
    }

    public static JsonArray updateJ(final JsonArray query, final JsonArray params, final String field) {
        return Compare.T.updateJ(query, params, field);
    }

    public static HRecord updateR(final HRecord record, final JsonObject params) {
        return Compare.T.updateR(record, params, () -> UUID.randomUUID().toString());
    }

    public static HRecord[] updateR(final HRecord[] record, final JsonArray array) {
        return updateR(record, array, KName.KEY);
    }

    public static HRecord[] updateR(final HRecord[] record, final JsonArray array, final String field) {
        final List<HRecord> recordList = Arrays.asList(record);
        return Compare.T.updateR(recordList, array, field).toArray(new HRecord[]{});
    }
}
