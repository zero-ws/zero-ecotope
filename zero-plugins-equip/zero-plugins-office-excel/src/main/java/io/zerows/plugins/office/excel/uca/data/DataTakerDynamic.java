package io.zerows.plugins.office.excel.uca.data;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;
import io.zerows.plugins.office.excel.atom.ExTenant;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-06-12
 */
class DataTakerDynamic implements DataTaker {
    private final ExTenant tenant;

    DataTakerDynamic(final ExTenant tenant) {
        this.tenant = tenant;
    }


    /*
     * {
     *      "source",
     *      "mapping"
     * }
     */
    @Override
    public Future<JsonArray> extract(final JsonArray dataA, final String name) {
        /* Source Processing */
        if (Objects.isNull(this.tenant)) {
            return Future.succeededFuture(dataA);
        } else {
            final JsonArray normalized;
            final JsonObject valueDefault = this.tenant.valueDefault();
            if (Ut.isNotNil(valueDefault)) {
                normalized = new JsonArray();
                // Append Global
                Ut.itJArray(dataA).forEach(json -> normalized.add(valueDefault.copy().mergeIn(json, true)));
            } else {
                normalized = dataA.copy();
            }

            // Extract Mapping
            final ConcurrentMap<String, String> first = this.tenant.dictionaryDefinition(name);
            if (first.isEmpty()) {
                return Future.succeededFuture(normalized);
            } else {
                // Directory
                return this.tenant.dictionary().compose(dataMap -> {
                    if (!dataMap.isEmpty()) {
                        /*
                         * mapping
                         * field = name
                         * dataMap
                         * name = JsonObject ( from = to )
                         * --->
                         *
                         * field -> JsonObject
                         */
                        final ConcurrentMap<String, JsonObject> combine
                            = Ut.elementZip(first, dataMap);

                        combine.forEach((key, value) -> Ut.itJArray(normalized).forEach(json -> {
                            final String fromValue = json.getString(key);
                            if (Ut.isNotNil(fromValue) && value.containsKey(fromValue)) {
                                final Object toValue = value.getValue(fromValue);
                                // Replace
                                json.put(key, toValue);
                            }
                        }));
                    }
                    return Future.succeededFuture(normalized);
                });
            }
        }
    }
}
