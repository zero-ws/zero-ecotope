package io.zerows.extension.runtime.report.uca.feature;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import org.osgi.framework.Bundle;

/**
 * 特征执行器，用于执行不同的特征相关信息
 *
 * @author lang : 2024-11-04
 */
public interface RFeature {
    Cc<String, RFeature> CC_SKELETON = Cc.openThread();

    static RFeature of(final KpFeature feature, final Bundle owner) {
        return AbstractFeature.of(feature, RFeatureImpl.class, owner);
    }

    static RFeature of(final KpFeature feature) {
        return of(feature, null);
    }

    Future<JsonObject> prepare(final JsonObject params);
}
