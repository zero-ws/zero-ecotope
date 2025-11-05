package io.zerows.extension.module.workflow.api.legacy;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.module.workflow.api.HighWay;
import io.zerows.extension.module.workflow.servicespec.TodoStub;
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
