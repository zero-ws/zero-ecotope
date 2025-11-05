package io.zerows.extension.module.workflow.component.conformity;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTicket;
import io.zerows.extension.module.workflow.domain.tables.pojos.WTodo;
import org.camunda.bpm.engine.task.Task;

/*
 * Task Gain that based join different lifecycle
 *
 * The constructor could be bind to ticket
 */
public interface Gain {

    static Gain starter(final WTicket ticket) {
        return new GainStart(ticket);
    }

    static Gain generator(final WTicket ticket) {
        return new GainGenerate(ticket);
    }

    Future<WTodo> buildAsync(JsonObject params, Task task, WTodo wTask);
}
