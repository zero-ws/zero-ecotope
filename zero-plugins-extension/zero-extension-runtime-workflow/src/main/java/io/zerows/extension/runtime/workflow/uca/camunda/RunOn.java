package io.zerows.extension.runtime.workflow.uca.camunda;

import io.zerows.extension.runtime.workflow.eon.WfPool;
import io.zerows.extension.runtime.workflow.eon.em.TodoStatus;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
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
