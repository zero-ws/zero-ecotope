package io.zerows.extension.module.workflow.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.workflow.domain.tables.WFlow;

import java.util.List;
import java.util.Map;

public class TypeOfWorkflowJsonObject extends TypeOfJsonObject {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // WFlow
            Map.of(
                WFlow.W_FLOW.AUTHORIZED_CONFIG.getName(), WFlow.W_FLOW.getName(),
                WFlow.W_FLOW.END_CONFIG.getName(), WFlow.W_FLOW.getName(),
                WFlow.W_FLOW.GENERATE_CONFIG.getName(), WFlow.W_FLOW.getName(),
                WFlow.W_FLOW.RUN_CONFIG.getName(), WFlow.W_FLOW.getName(),
                WFlow.W_FLOW.START_CONFIG.getName(), WFlow.W_FLOW.getName(),
                WFlow.W_FLOW.UI_ASSIST.getName(), WFlow.W_FLOW.getName(),
                WFlow.W_FLOW.UI_CONFIG.getName(), WFlow.W_FLOW.getName(),
                WFlow.W_FLOW.UI_LINKAGE.getName(), WFlow.W_FLOW.getName()
            )
        );
    }
}
