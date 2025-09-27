package io.zerows.extension.runtime.report.uca.feature;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;

/**
 * @author lang : 2024-11-04
 */
class RFeatureGlobal extends AbstractFeature implements RFeature {

    RFeatureGlobal(final KpFeature feature) {
        super(feature);
    }

    @Override
    public Future<JsonObject> prepare(final JsonObject params) {
        return null;
    }
}
