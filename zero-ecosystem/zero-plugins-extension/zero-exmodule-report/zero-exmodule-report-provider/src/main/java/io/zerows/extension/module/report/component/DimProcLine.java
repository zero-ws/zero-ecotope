package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.report.common.RDimension;
import io.zerows.extension.module.report.domain.tables.pojos.KpDimension;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2024-10-29
 */
class DimProcLine extends DimProcBase {

    DimProcLine(final HBundle owner) {
        super(owner);
    }

    @Override
    public Future<RDimension> dimAsync(final JsonObject params, final JsonArray source, final KpDimension dimension) {
        return super.dimAsync(params, source, dimension);
    }
}
