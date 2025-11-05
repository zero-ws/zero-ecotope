package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.report.domain.tables.pojos.KpFeature;

/**
 * @author lang : 2024-11-04
 */
class FeatureRGlobal extends FeatureRBase implements FeatureR {

    FeatureRGlobal(final KpFeature feature) {
        super(feature);
    }

    @Override
    public Future<JsonObject> prepare(final JsonObject params) {
        return null;
    }
}
