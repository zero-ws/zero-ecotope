package io.zerows.extension.module.workflow.component.camunda;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.common.em.TodoStatus;
import io.zerows.extension.module.workflow.metadata.WTransition;
import io.zerows.extension.module.workflow.metadata.WfPool;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface RunOn {

    static RunOn get() {
        return WfPool.CC_RUN.pick(RunEngine::new);
    }

    // Start
    Future<ProcessInstance> startAsync(JsonObject params, WTransition transition);

    // Run
    Future<ProcessInstance> moveAsync(JsonObject params, WTransition transition);

    // Stop
    Future<Boolean> stopAsync(TodoStatus status, WTransition transition);

    // ---------------------- Running Checking -----------------------

}
