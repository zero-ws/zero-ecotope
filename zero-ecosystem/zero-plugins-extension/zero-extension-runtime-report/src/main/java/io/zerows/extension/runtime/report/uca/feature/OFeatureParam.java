package io.zerows.extension.runtime.report.uca.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-27
 */
class OFeatureParam implements OFeature {
    @Override
    public Future<ConcurrentMap<String, Object>> outAsync(final JsonArray dataSource, final JsonObject params,
                                                          final KpFeature feature) {
        final String valuePath = feature.getValuePath();
        final String fieldParam = valuePath.split(":")[1];

        final Object value = params.getValue(fieldParam);

        final JsonObject valueConfig = Ut.toJObject(feature.getValueConfig());
        final Object valueResult = T.formatValue(value, valueConfig);
        // 这种类型每条记录都应该有
        final ConcurrentMap<String, Object> resultMap = new ConcurrentHashMap<>();
        Ut.itJArray(dataSource).forEach(item -> {
            final String key = Ut.valueString(item, KName.KEY);
            resultMap.put(key, valueResult);
        });
        return Ux.future(resultMap);
    }
}
