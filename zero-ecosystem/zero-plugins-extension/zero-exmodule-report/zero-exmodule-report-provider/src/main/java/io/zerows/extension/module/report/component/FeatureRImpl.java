package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.report.common.em.EmReport;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;

/**
 * @author lang : 2024-11-04
 */
class FeatureRImpl extends FeatureRBase implements FeatureR {

    FeatureRImpl(final KpFeature feature) {
        super(feature);
    }

    @Override
    public Future<JsonObject> prepare(final JsonObject params) {
        // 不带特征的处理流程

        // 是否全局
        if (EmReport.FeatureType.GLOBAL == this.type) {
            // 全局特征执行
            final FeatureR executor = this.of(FeatureRGlobal.class);
            return executor.prepare(params);
        }
        return null;
    }
}
