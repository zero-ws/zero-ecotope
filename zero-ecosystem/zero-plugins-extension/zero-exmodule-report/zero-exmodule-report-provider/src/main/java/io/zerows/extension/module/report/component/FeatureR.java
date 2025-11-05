package io.zerows.extension.module.report.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;
import io.zerows.specification.development.compiled.HBundle;

/**
 * 特征执行器，用于执行不同的特征相关信息
 *
 * @author lang : 2024-11-04
 */
public interface FeatureR {
    Cc<String, FeatureR> CC_SKELETON = Cc.openThread();

    static FeatureR of(final KpFeature feature, final HBundle owner) {
        return FeatureRBase.of(feature, FeatureRImpl.class, owner);
    }

    static FeatureR of(final KpFeature feature) {
        return of(feature, null);
    }

    Future<JsonObject> prepare(final JsonObject params);
}
