package io.zerows.extension.module.workflow.component.transition;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTodo;
import io.zerows.extension.module.workflow.metadata.WTransition;

/**
 * @author lang : 2023-06-29
 */
class VmEmpty implements Vm {
    @Override
    public void generate(final WTodo generated, final WTodo wTask, final WTicket ticket) {

    }

    @Override
    public void end(final JsonObject normalized, final WTransition transition) {

    }
}
