package io.zerows.extension.module.report.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.report.common.RGeneration;
import io.zerows.extension.module.report.domain.tables.pojos.KpReport;
import io.zerows.extension.module.report.domain.tables.pojos.KpReportInstance;
import io.zerows.support.Ut;

import java.time.LocalDateTime;

/**
 * @author lang : 2024-11-26
 */
class StepGeneratorBelong extends StepGeneratorBase {

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
     * @return 处理后的报表实例
     */
    @Override
    public Future<KpReportInstance> build(final KpReportInstance instance, final JsonObject request, final JsonArray sourceData) {
        final KpReport report = this.metadata().reportMeta();
        instance.setReportId(report.getId());

        final String user = Ut.valueString(request, KName.USER);        // user
        instance.setReportBy(user);
        instance.setReportAt(LocalDateTime.now());

        /*
         * 补充 refType / refId
         */
        instance.setRefType(report.getClass().getName());
        instance.setRefId(report.getId());
        return Future.succeededFuture(instance);
    }
}
