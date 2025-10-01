package io.zerows.extension.runtime.report.uca.combiner;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.report.atom.RGeneration;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReport;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReportInstance;
import io.zerows.extension.runtime.report.eon.em.EmReport;

/**
 * @author lang : 2024-11-25
 */
class StepGeneratorNorm extends AbstractStepGenerator {
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
     *
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
        return Ut.future(instance);
    }
}
