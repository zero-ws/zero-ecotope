package io.zerows.extension.module.mbsecore.component.reference;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.modeling.EmValue;
import io.zerows.platform.metadata.RResult;
import io.zerows.specification.modeling.HRecord;
import io.zerows.support.Ut;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * ## Reference Result Processor
 * <p>
 * ### 1. Intro
 * <p>
 * This class could combine the results based join the `rule` that defined.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class RayResult {
    /**
     * Combine single record based join defined code logical
     *
     * @param record     {@link HRecord} Result records
     * @param joinData   {@link java.util.concurrent.ConcurrentMap} Reference data map
     * @param joinResult {@link java.util.concurrent.ConcurrentMap} Reference rule map
     * @return {@link HRecord}
     */
    static HRecord combine(final HRecord record, final ConcurrentMap<String, JsonArray> joinData,
                           final ConcurrentMap<String, RResult> joinResult) {
        compressData(joinData, joinResult).forEach((field, processed) -> {
            final RResult result = joinResult.get(field);
            /* JAmb */
            final ConcurrentMap<String, RayAmb> grouped = groupData(processed, result);
            /* Combine */
            combine(record, field, grouped, result);
        });
        return record;
    }

    /**
     * Combine multi record based join defined code logical
     *
     * @param records    {@link HRecord}[] Result records
     * @param joinData   {@link java.util.concurrent.ConcurrentMap} Reference data map
     * @param joinResult {@link java.util.concurrent.ConcurrentMap} Reference rule map
     * @return {@link HRecord}[]
     */
    static HRecord[] combine(final HRecord[] records,
                             final ConcurrentMap<String, JsonArray> joinData,
                             final ConcurrentMap<String, RResult> joinResult) {
        compressData(joinData, joinResult).forEach((field, processed) -> {
            final RResult result = joinResult.get(field);
            /* JAmb */
            final ConcurrentMap<String, RayAmb> grouped = groupData(processed, result);
            /* Iterator for each record */
            Arrays.stream(records).forEach(record -> combine(record, field, grouped, result));
        });
        return records;
    }

    private static void combine(final HRecord record, final String field, final ConcurrentMap<String, RayAmb> groupData, final RResult result) {
        /* Key */
        final String keyRecord = keyRecord(record, result.joined());
        /* Combined */
        final RayAmb amb = groupData.get(keyRecord);
        if (Objects.isNull(amb)) {
            /*
             * Apply Default Value
             *
             * 1. Object = {}
             * 2. Array = []
             * */
            final EmValue.Format format = result.format();
            if (EmValue.Format.JsonArray == format) {
                record.add(field, new JsonArray());
            } else if (EmValue.Format.JsonObject == format) {
                record.add(field, new JsonObject());
            }
        } else {
            if (amb.isValid()) {
                combine(record, field, amb, result);
            }
        }
    }

    private static void combine(final HRecord record, final String field, final RayAmb amb, final RResult result) {
        /* Amb */
        final EmValue.Format format = result.format();
        if (EmValue.Format.JsonArray == format) {
            /*
             * JsonArray extract.
             */
            final JsonArray extract = amb.dataT();
            record.add(field, extract);
        } else {
            /*
             * JsonObject extract.
             */
            final JsonObject extract = amb.dataT();
            if (Ut.isNotNil(extract)) {
                if (EmValue.Format.JsonObject == format) {
                    /* JsonObject */
                    record.add(field, extract);
                } else {
                    /* Elementary */
                    final String sourceField = result.sourceField();
                    final Object value = extract.getValue(sourceField);
                    if (Objects.nonNull(value)) {
                        record.add(field, value);
                    }
                }
            }
        }
    }

    /**
     * Grouped Data based join definition.
     *
     * @param data   {@link JsonArray}
     * @param result {@link RResult}
     * @return {@link java.util.concurrent.ConcurrentMap}
     */
    private static ConcurrentMap<String, RayAmb> groupData(final JsonArray data, final RResult result) {
        /*
         * Result type came from `result`.
         */
        final ConcurrentMap<String, RayAmb> groupedData = new ConcurrentHashMap<>();
        final Class<?> type = result.typeData();
        if (JsonArray.class == type) {
            /*
             * field = JsonArray
             */
            Ut.itJArray(data).forEach(json -> {
                final String key = keyReference(json, result.joined());
                if (Ut.isNotNil(key)) {
                    Cc.pool(groupedData, key, () -> new RayAmb().data(new JsonArray())).add(json);
                }
            });
        } else {
            /*
             * field = JsonObject
             */
            Ut.itJArray(data).forEach(json -> {
                final String key = keyReference(json, result.joined());
                if (Ut.isNotNil(key)) {
                    groupedData.put(key, new RayAmb().data(json));
                }
            });
        }
        return groupedData;
    }

    private static String keyReference(final JsonObject item, final List<Kv<String, String>> joined) {
        return keyJoin(item::getValue, Kv::key, joined);
    }

    private static String keyRecord(final HRecord record, final List<Kv<String, String>> joined) {
        return keyJoin(record::get, Kv::value, joined);
    }

    private static String keyJoin(final Function<String, Object> function,
                                  final Function<Kv<String, String>, String> supplier,
                                  final List<Kv<String, String>> joined) {
        final StringBuilder key = new StringBuilder();
        joined.forEach(kv -> {
            final Object value = function.apply(supplier.apply(kv));
            if (Objects.nonNull(value)) {
                key.append(value);
            }
        });
        return key.toString();
    }

    /**
     * Run ruler for the data array.
     *
     * @param joinData   {@link java.util.concurrent.ConcurrentMap} join data
     * @param joinResult {@link java.util.concurrent.ConcurrentMap} join result definition.
     * @return {@link java.util.concurrent.ConcurrentMap}
     */
    private static ConcurrentMap<String, JsonArray> compressData(
        final ConcurrentMap<String, JsonArray> joinData,
        final ConcurrentMap<String, RResult> joinResult
    ) {
        final ConcurrentMap<String, JsonArray> compressed = new ConcurrentHashMap<>();
        joinData.forEach((field, each) -> {
            final RResult result = joinResult.get(field);
            final JsonArray dataArray = joinData.getOrDefault(field, new JsonArray());
            assert Objects.nonNull(result) : "Here result should not be null.";
            final JsonArray processed = result.runRuler(dataArray);
            compressed.put(field, processed);
        });
        return compressed;
    }
}
