package io.zerows.extension.runtime.report.uca.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VString;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.unity.Ux;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-27
 */
class OFeatureData implements OFeature {

    @Override
    public Future<ConcurrentMap<String, Object>> outAsync(final JsonArray dataSource, final JsonObject params,
                                                          final KpFeature feature) {
        final String valuePath = feature.getValuePath();
        final String fieldExpr = valuePath.split(":")[1];
        final String[] fieldPath = fieldExpr.split(VString.SLASH);

        final ConcurrentMap<String, Object> resultMap = new ConcurrentHashMap<>();
        Ut.itJArray(dataSource).forEach(item -> {
            final String key = Ut.valueString(item, KName.KEY);
            final Object valueResult = Ut.visitString(item, fieldPath);

            if (Objects.nonNull(valueResult)) {
                final JsonObject valueConfig = Ut.toJObject(feature.getValueConfig());
                final Object valueFinal = T.formatValue(valueResult, valueConfig);
                resultMap.put(key, valueFinal);
            }
        });
        return Ux.future(resultMap);
    }
}
