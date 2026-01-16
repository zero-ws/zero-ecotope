package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.common.RGeneration;
import io.zerows.extension.module.report.common.em.EmReport;
import io.zerows.extension.module.report.domain.tables.pojos.KpReport;
import io.zerows.extension.module.report.domain.tables.pojos.KpReportInstance;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-11-25
 */
class StepGeneratorNorm extends StepGeneratorBase {
    StepGeneratorNorm(final RGeneration generation) {
        super(generation);
    }

    /**
     * <pre><code>
     *     name             = report name + YYYYMMdd
     *     status
     *     type             = report / code
     *     title
     *       subtitle
     *       extra
     *       description
     * </code></pre>
     *
     * @param instance   新报表实例
     * @param request    请求数据
     * @param sourceData 源数据
     * @return 处理后的报表实例
     */
    @Override
    public Future<KpReportInstance> build(final KpReportInstance instance, final JsonObject request,
                                          final JsonArray sourceData) {
        final KpReport report = this.metadata().reportMeta();

        final String time = Ut.valueString(request, "time");
        final String name = time + report.getName();
        instance.setName(name);

        instance.setStatus(EmReport.UcaStatus.WAITING.name());      // 固定 WAITING，后续保存后才是 ACTIVE
        instance.setType(report.getCode());                         // REPORT 对应的 code

        this.parseAndExtract(request, report.getTitle(), instance::setTitle);

        final JsonObject reportConfig = Ut.toJObject(report.getReportConfig());
        // extra / subtitle / description
        {
            final JsonObject instanceConfig = Ut.valueJObject(reportConfig, KName.INSTANCE);
            final String fieldExtra = Ut.valueString(instanceConfig, "extra");
            this.parseAndExtract(request, fieldExtra, instance::setExtra);
            final String fieldSubtitle = Ut.valueString(instanceConfig, "subtitle");
            this.parseAndExtract(request, fieldSubtitle, instance::setSubtitle);
            final String fieldDescription = Ut.valueString(instanceConfig, "description");
            this.parseAndExtract(request, fieldDescription, instance::setDescription);
        }
        return Future.succeededFuture(instance);
    }
}
