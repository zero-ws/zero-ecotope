package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.common.RGeneration;
import io.zerows.extension.module.report.domain.tables.pojos.KpReport;
import io.zerows.extension.module.report.domain.tables.pojos.KpReportInstance;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-11-26
 */
class StepGeneratorAudit extends StepGeneratorBase {
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
        return Future.succeededFuture(instance);
    }
}
