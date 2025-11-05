package io.zerows.extension.module.report.component;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogO;
import io.zerows.extension.module.report.common.RGeneration;
import io.zerows.extension.module.report.domain.tables.pojos.KpReportInstance;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-11-25
 */
public interface StepGenerator {

    Cc<String, StepGenerator> CC_SKELETON = Cc.openThread();

    static StepGenerator of(final RGeneration generation) {
        return StepGeneratorBase.of(generation, StepGeneratorFacade.class);
    }

    Future<KpReportInstance> build(KpReportInstance instance, JsonObject request, JsonArray sourceData);

    default LogO logger() {
        return Ut.Log.data(this.getClass());
    }
}
