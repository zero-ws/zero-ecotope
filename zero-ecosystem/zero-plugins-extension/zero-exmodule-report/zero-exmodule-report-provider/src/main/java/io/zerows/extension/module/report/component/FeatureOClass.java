package io.zerows.extension.module.report.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.module.report.plugins.ROutComponent;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-11-27
 */
class FeatureOClass implements FeatureO {

    private static final Cc<String, ROutComponent> CC_OUT = Cc.openThread();

    @Override
    public Future<ConcurrentMap<String, Object>> outAsync(final JsonArray dataSource, final JsonObject params,
                                                          final KpFeature feature) {
        final String outComponentCls = feature.getOutComponent();
        if (Ut.isNil(outComponentCls)) {
            return Ut.future(new ConcurrentHashMap<>());
        }
        final JsonObject outConfig = Ut.toJObject(feature.getOutConfig());
        final ROutComponent outComponent = CC_OUT.pick(() -> Ut.instance(outComponentCls), outComponentCls);
        if (Objects.isNull(outComponent)) {
            return Ut.future(new ConcurrentHashMap<>());
        }

        final JsonObject parameters = new JsonObject();
        parameters.put(KName.INPUT, params);
        parameters.put(KName.CONFIG, outConfig);
        return outComponent.outAsync(dataSource, parameters).compose(result -> {
            if (Objects.isNull(result)) {
                return Ut.future(new ConcurrentHashMap<>());
            }
            return Ut.future(result);
        });
    }
}
