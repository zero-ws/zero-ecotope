package io.zerows.extension.runtime.report.uca.combiner;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.report.atom.RGeneration;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReportInstance;
import io.zerows.module.metadata.uca.logging.OLog;

/**
 * @author lang : 2024-11-25
 */
public interface StepGenerator {

    Cc<String, StepGenerator> CC_SKELETON = Cc.openThread();

    static StepGenerator of(final RGeneration generation) {
        return AbstractStepGenerator.of(generation, StepGeneratorFacade.class);
    }

    Future<KpReportInstance> build(KpReportInstance instance, JsonObject request, JsonArray sourceData);

    default OLog logger() {
        return Ut.Log.data(this.getClass());
    }
}
