package io.zerows.extension.module.report.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.report.domain.tables.KpDataSet;
import io.zerows.extension.module.report.domain.tables.KpDimension;
import io.zerows.extension.module.report.domain.tables.KpFeature;
import io.zerows.extension.module.report.domain.tables.KpReport;
import io.zerows.extension.module.report.domain.tables.KpReportInstance;

import java.util.List;
import java.util.Map;

public class TypeOfReportJsonObject extends TypeOfJsonObject {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // KpDataSet
            Map.of(
                KpDataSet.KP_DATA_SET.DATA_CONFIG.getName(), KpDataSet.KP_DATA_SET.getName(),
                KpDataSet.KP_DATA_SET.DATA_FIELD.getName(), KpDataSet.KP_DATA_SET.getName(),
                KpDataSet.KP_DATA_SET.DATA_QUERY.getName(), KpDataSet.KP_DATA_SET.getName(),
                KpDataSet.KP_DATA_SET.DATA_SOURCE.getName(), KpDataSet.KP_DATA_SET.getName(),
                KpDataSet.KP_DATA_SET.SOURCE_CONFIG.getName(), KpDataSet.KP_DATA_SET.getName()
            ),
            // KpDimension
            Map.of(
                KpDimension.KP_DIMENSION.CHART_CONFIG.getName(), KpDimension.KP_DIMENSION.getName(),
                KpDimension.KP_DIMENSION.DATA_FIELD.getName(), KpDimension.KP_DIMENSION.getName(),
                KpDimension.KP_DIMENSION.DATA_GROUP.getName(), KpDimension.KP_DIMENSION.getName(),
                KpDimension.KP_DIMENSION.DATA_OUTPUT.getName(), KpDimension.KP_DIMENSION.getName(),
                KpDimension.KP_DIMENSION.DATA_QUERY.getName(), KpDimension.KP_DIMENSION.getName()
            ),
            // KpFeature
            Map.of(
                KpFeature.KP_FEATURE.VALUE_CONFIG.getName(), KpFeature.KP_FEATURE.getName(),
                KpFeature.KP_FEATURE.IN_CONFIG.getName(), KpFeature.KP_FEATURE.getName(),
                KpFeature.KP_FEATURE.OUT_CONFIG.getName(), KpFeature.KP_FEATURE.getName()
            ),
            // KpReport
            Map.of(
                KpReport.KP_REPORT.REPORT_CONFIG.getName(), KpReport.KP_REPORT.getName(),
                KpReport.KP_REPORT.REPORT_PARAM.getName(), KpReport.KP_REPORT.getName()
            ),
            // KpReportInstance
            Map.of(
                KpReportInstance.KP_REPORT_INSTANCE.REPORT_CONTENT.getName(), KpReportInstance.KP_REPORT_INSTANCE.getName()
            )
        );
    }
}
