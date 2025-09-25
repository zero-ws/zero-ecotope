package io.zerows.extension.runtime.report.uca.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-27
 */
class OFeatureRefer implements OFeature {
    @Override
    public Future<ConcurrentMap<String, Object>> outAsync(final JsonArray dataSource, final JsonObject params, final KpFeature feature) {
        final String valuePath = feature.getValuePath();
        final String fieldExpr = valuePath.split(":")[1];
        final ConcurrentMap<String, Object> resultMap = new ConcurrentHashMap<>();

        final JsonObject valueConfig = Ut.toJObject(feature.getValueConfig());
        final JsonObject filterObj = Ut.valueJObject(valueConfig, "filter");

        Ut.itJArray(dataSource).filter(record -> {
            final JsonObject subRecord = Ut.elementSubset(record, filterObj.fieldNames());
            return subRecord.equals(filterObj);
        }).forEach(item -> {
            final String key = Ut.valueString(item, KName.KEY);
            final Object valueResult;
            if (fieldExpr.contains("`")) {
                // 表达式处理
                valueResult = Ut.fromExpression(fieldExpr, item);
            } else {
                // 非表达式
                valueResult = item.getValue(fieldExpr);
            }
            if (Objects.nonNull(valueResult)) {
                final Object valueFinal = T.formatValue(valueResult, valueConfig);
                resultMap.put(key, valueFinal);
            }
        });
        return Ux.future(resultMap);
    }
}
