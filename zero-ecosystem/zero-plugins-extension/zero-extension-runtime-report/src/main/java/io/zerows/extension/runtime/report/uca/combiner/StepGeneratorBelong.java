package io.zerows.extension.runtime.report.uca.combiner;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import io.zerows.extension.runtime.report.atom.RGeneration;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReport;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpReportInstance;

import java.time.LocalDateTime;

/**
 * @author lang : 2024-11-26
 */
class StepGeneratorBelong extends AbstractStepGenerator {

    StepGeneratorBelong(final RGeneration generation) {
        super(generation);
    }

    /**
     * <pre><code>
     *     reportId
     *     reportBy
     *     reportAt
     *     refType
     *     refId
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
        instance.setReportId(report.getKey());

        final String user = Ut.valueString(request, KName.USER);        // user
        instance.setReportBy(user);
        instance.setReportAt(LocalDateTime.now());

        /*
         * 补充 refType / refId
         */
        instance.setRefType(report.getClass().getName());
        instance.setRefId(report.getKey());
        return Ut.future(instance);
    }
}
