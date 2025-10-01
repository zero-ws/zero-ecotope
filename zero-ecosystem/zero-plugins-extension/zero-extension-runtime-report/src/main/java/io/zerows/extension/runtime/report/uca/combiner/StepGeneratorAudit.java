package io.zerows.extension.runtime.report.uca.combiner;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.report.atom.RGeneration;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReport;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReportInstance;

/**
 * @author lang : 2024-11-26
 */
class StepGeneratorAudit extends AbstractStepGenerator {
    StepGeneratorAudit(final RGeneration generation) {
        super(generation);
    }

    /**
     * 特殊处理
     * <pre><code>
     *     active
     *     sigma
     *     metadata
     *     language
     *     createdAt
     *     createdBy
     *     updatedAt
     *     updatedBy
     * </code></pre>
     *
     * @param instance   新报表实例
     * @param request    请求数据
     * @param sourceData 源数据
     *
     * @return 处理后的报表实例
     */
    @Override
    public Future<KpReportInstance> build(final KpReportInstance instance, final JsonObject request, final JsonArray sourceData) {
        final KpReport report = this.metadata().reportMeta();

        instance.setActive(Boolean.FALSE);
        instance.setSigma(report.getSigma());
        instance.setLanguage(report.getLanguage());

        final JsonObject reportConfig = Ut.toJObject(report.getReportConfig());
        // metadata
        final JsonObject metadata = Ut.valueJObject(reportConfig, KName.METADATA);
        if (Ut.isNotNil(metadata)) {
            instance.setMetadata(metadata.encode());
        }
        return Ut.future(instance);
    }
}
