package io.zerows.extension.runtime.workflow.agent.api.legacy;

import io.zerows.extension.runtime.workflow.eon.HighWay;
import io.zerows.extension.runtime.workflow.agent.service.TodoStub;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import jakarta.inject.Inject;

@Queue
public class TodoActor {
    @Inject
    private transient TodoStub todoStub;

    @Address(HighWay.Todo.BY_ID)
    public Future<JsonObject> fetchTodo(final String key) {
        return this.todoStub.fetchTodo(key);
    }
}
