package io.zerows.extension.runtime.report.uca.process;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.report.atom.RDimension;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpDimension;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-10-29
 */
class DimProcLine extends AbstractDimProc {

    DimProcLine(final Bundle owner) {
        super(owner);
    }

    @Override
    public Future<RDimension> dimAsync(final JsonObject params, final JsonArray source, final KpDimension dimension) {
        return super.dimAsync(params, source, dimension);
    }
}
