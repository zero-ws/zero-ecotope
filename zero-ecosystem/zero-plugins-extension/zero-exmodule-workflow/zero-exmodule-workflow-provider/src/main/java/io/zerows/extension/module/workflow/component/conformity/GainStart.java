package io.zerows.extension.module.workflow.component.conformity;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTodo;
import io.zerows.program.Ux;
import org.camunda.bpm.engine.task.Task;

public class GainStart extends AbstractGain {

    GainStart(final WTicket ticket) {
        super(ticket);
    }

    @Override
    public Future<WTodo> buildAsync(final JsonObject params, final Task task, final WTodo wTask) {
        // 0. Keep the same acceptedBy / toUser findRunning and do nothing
        // 1. InJson -> WTodo
        final WTodo todo = Ux.fromJson(params, WTodo.class);

        // 2. Connect Camunda
        this.bridgeTask(todo, task, this.ticket.getKey());

        // 3. TraceOrder = 1 and of serial/code
        todo.setTraceOrder(1);
        return Ux.future(todo);
    }
}
