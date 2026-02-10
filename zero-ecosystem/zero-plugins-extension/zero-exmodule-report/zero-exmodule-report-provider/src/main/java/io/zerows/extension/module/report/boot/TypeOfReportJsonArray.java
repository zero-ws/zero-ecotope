package io.zerows.extension.module.report.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonArray;
import io.zerows.extension.module.report.domain.tables.KpReportInstance;

import java.util.List;
import java.util.Map;

public class TypeOfReportJsonArray extends TypeOfJsonArray {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // KpReportInstance
            Map.of(
                KpReportInstance.KP_REPORT_INSTANCE.REPORT_DATA.getName(), KpReportInstance.KP_REPORT_INSTANCE.getName()
            )
        );
    }
}
