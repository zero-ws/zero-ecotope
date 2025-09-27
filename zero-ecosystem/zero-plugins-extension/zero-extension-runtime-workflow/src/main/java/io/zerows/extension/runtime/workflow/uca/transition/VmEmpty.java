package io.zerows.extension.runtime.workflow.uca.transition;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
import io.zerows.extension.runtime.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.runtime.workflow.domain.tables.pojos.WTodo;

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
