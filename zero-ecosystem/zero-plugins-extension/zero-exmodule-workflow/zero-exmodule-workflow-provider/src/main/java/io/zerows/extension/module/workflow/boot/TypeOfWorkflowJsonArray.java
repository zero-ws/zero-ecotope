package io.zerows.extension.module.workflow.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonArray;
import io.zerows.extension.module.workflow.domain.tables.WTicket;

import java.util.List;
import java.util.Map;

public class TypeOfWorkflowJsonArray extends TypeOfJsonArray {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // WTicket
            Map.of(
                WTicket.W_TICKET.MODEL_CHILD.getName(), WTicket.W_TICKET.getName()
            )
        );
    }
}
