package io.zerows.extension.runtime.report.uca.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.runtime.report.eon.em.EmReport;

/**
 * @author lang : 2024-11-04
 */
class RFeatureImpl extends AbstractFeature implements RFeature {

    RFeatureImpl(final KpFeature feature) {
        super(feature);
    }

    @Override
    public Future<JsonObject> prepare(final JsonObject params) {
        // 不带特征的处理流程
        
        // 是否全局
        if (EmReport.FeatureType.GLOBAL == this.type) {
            // 全局特征执行
            final RFeature executor = this.of(RFeatureGlobal.class);
            return executor.prepare(params);
        }
        return null;
    }
}
