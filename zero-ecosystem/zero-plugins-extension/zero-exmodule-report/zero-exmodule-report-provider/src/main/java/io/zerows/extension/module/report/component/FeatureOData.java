package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-27
 */
class FeatureOData implements FeatureO {

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
