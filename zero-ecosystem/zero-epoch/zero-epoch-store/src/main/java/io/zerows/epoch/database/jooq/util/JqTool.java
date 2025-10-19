package io.zerows.epoch.database.jooq.util;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogO;
import io.zerows.component.qr.syntax.Ir;
import io.zerows.epoch.metadata.MMPojo;
import io.zerows.epoch.metadata.MMPojoMapping;
import io.zerows.platform.constant.VName;
import io.zerows.platform.constant.VString;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JqTool {

    private static final LogO LOGGER = Ut.Log.database(JqTool.class);

    public static <T> CompositeFuture joinAsync(final JsonObject criteria, final JsonObject data, final JqFlow flow) {
        final Future<JsonObject> criteriaFuture = flow.inputQrJAsync(criteria);
        final Future<T> dataFuture = flow.inputAsync(data);
        return Future.join(criteriaFuture, dataFuture);
    }

    public static <T> CompositeFuture joinAsync(final JsonObject criteria, final JsonArray data, final JqFlow flow) {
        final Future<JsonObject> criteriaFuture = flow.inputQrJAsync(criteria);
        final Future<List<T>> dataFuture = flow.inputAsync(data);
        return Future.join(criteriaFuture, dataFuture);
    }

    public static Ir qr(final JsonObject envelop, final String pojo) {
        final JsonObject data = envelop.copy();
        if (Ut.isNil(pojo)) {
            return Ir.create(data);
        } else {
            // Projection Process
            final MMPojo mojo = MMPojoMapping.create(JqTool.class).mount(pojo).mojo();
            return qr(data, mojo);
        }
    }

    public static Ir qr(final JsonObject data, final MMPojo mojo) {
        return qr(data, mojo, new HashSet<>());
    }

    public static Ir qr(final JsonObject data, final MMPojo mojo, final Set<String> ignoreSet) {
        if (data.containsKey("projection")) {
            data.put("projection", projection(data.getJsonArray("projection"), mojo, ignoreSet));
        }
        if (data.containsKey("sorter")) {
            data.put("sorter", sorter(data.getJsonArray("sorter"), mojo, ignoreSet));
        }
        if (data.containsKey("criteria")) {
            data.put("criteria", criteria(data.getJsonObject("criteria"), mojo, ignoreSet));
        }
        LOGGER.info(INFO.INQUIRY_MESSAGE, data.encode());
        return Ir.create(data);
    }

    public static JsonObject criteria(final JsonObject criteria, final String pojo) {
        final Ir qr = qr(new JsonObject().put(VName.KEY_CRITERIA, criteria), pojo);
        return Objects.isNull(qr.getCriteria()) ? new JsonObject() : qr.getCriteria().toJson();
    }

    public static JsonObject criteria(final JsonObject criteria, final MMPojo mojo) {
        return criteria(criteria, mojo, new HashSet<>());
    }


    public static JsonObject criteria(final JsonObject criteria, final MMPojo mojo, final Set<String> ignoreSet) {
        final JsonObject condition = new JsonObject();
        final ConcurrentMap<String, String> mapping = joinMapping(mojo, ignoreSet);
        for (final String field : criteria.fieldNames()) {
            // Filter processed
            final String key = field.contains(VString.COMMA) ? field.split(VString.COMMA)[0] : field;
            final String targetField;
            if (mapping.containsKey(key)) {
                if (field.contains(VString.COMMA)) {
                    targetField = mapping.get(key) + VString.COMMA + field.split(VString.COMMA)[1];
                } else {
                    targetField = mapping.get(key);
                }
                // Ignore non-existing field in mapping here to avoid SQL errors.
                condition.put(targetField, criteria.getValue(field));
            } else {
                // JqTool Engine Needed, Support Tree
                if (Ut.isJObject(criteria.getValue(field)) || field.equals(VString.EMPTY)) {
                    if (Ut.isJObject(criteria.getValue(field))) {
                        final JsonObject valueJson = criteria.getJsonObject(field);
                        condition.put(field, criteria(valueJson, mojo, ignoreSet));
                    } else {
                        condition.put(field, criteria.getValue(field));
                    }
                } else {
                    condition.put(field, criteria.getValue(field));
                }
            }

        }
        return condition;
    }

    private static JsonArray projection(final JsonArray projections, final MMPojo mojo, final Set<String> ignoreSet) {
        final JsonArray result = new JsonArray();
        final ConcurrentMap<String, String> mapping = joinMapping(mojo, ignoreSet);
        Ut.itJArray(projections, String.class, (item, index) ->
            result.add((null == mapping.get(item)) ? item : mapping.get(item)));
        return result;
    }

    private static JsonArray sorter(final JsonArray sorter, final MMPojo mojo, final Set<String> ignoreSet) {
        final JsonArray sorters = new JsonArray();
        final ConcurrentMap<String, String> mapping = joinMapping(mojo, ignoreSet);
        Ut.itJArray(sorter, String.class, (item, index) -> {
            final String key = item.contains(VString.COMMA) ? item.split(VString.COMMA)[0] : item;
            if (mapping.containsKey(key)) {
                final String targetField = mapping.get(key);
                if (item.contains(VString.COMMA)) {
                    sorters.add(targetField + VString.COMMA + item.split(VString.COMMA)[1]);
                } else {
                    sorters.add(targetField + VString.COMMA + "ASC");
                }
            } else {
                sorters.add(item);
            }
        });
        return sorters;
    }

    private static ConcurrentMap<String, String> joinMapping(final MMPojo mojo, final Set<String> ignoreSet) {
        // The new mapping that should be transfer
        // sigma -> zSigma
        final ConcurrentMap<String, String> mapping = mojo.getIn();
        if (Objects.isNull(ignoreSet) || ignoreSet.isEmpty()) {
            return mapping;
        } else {
            final ConcurrentMap<String, String> filteredMap = new ConcurrentHashMap<>();
            mapping.forEach((key, value) -> {
                // The ignoreSet should not be in mapping
                if (!ignoreSet.contains(key)) {
                    filteredMap.put(key, value);
                }
            });
            return filteredMap;
        }
    }
}
