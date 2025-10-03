package io.zerows.program;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.specification.modeling.HRecord;
import io.zerows.support.Ut;

import java.util.List;

/**
 * @author lang : 2023-06-11
 */
class _Update extends _To {

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
        return Ut.cloneT(input);
    }

    public static <T> T updateT(final T query, final JsonObject params) {
        return Ut.updateT(query, params);
    }

    public static <T> List<T> updateT(final List<T> query, final JsonArray params) {
        return Ut.updateT(query, params, KName.KEY);
    }

    public static <T> List<T> updateT(final List<T> query, final JsonArray params, final String field) {
        return Ut.updateT(query, params, field);
    }

    public static JsonArray updateJ(final JsonArray query, final JsonArray params) {
        return Ut.updateJ(query, params, KName.KEY);
    }

    public static JsonArray updateJ(final JsonArray query, final JsonArray params, final String field) {
        return Ut.updateJ(query, params, field);
    }

    public static HRecord updateR(final HRecord record, final JsonObject params) {
        return Ut.updateR(record, params);
    }

    public static HRecord[] updateR(final HRecord[] record, final JsonArray array) {
        return updateR(record, array, KName.KEY);
    }

    public static HRecord[] updateR(final HRecord[] record, final JsonArray array, final String field) {
        return Ut.updateR(record, array, field);
    }
}
