package io.zerows.extension.runtime.workflow.uca.component;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.workflow.atom.runtime.WRequest;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
import io.zerows.extension.runtime.workflow.exception._80604Exception409InValidStart;
import io.zerows.extension.runtime.workflow.uca.camunda.RunOn;
import io.zerows.extension.runtime.workflow.uca.central.AbstractMoveOn;
import org.camunda.bpm.engine.repository.ProcessDefinition;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class MoveOnStart extends AbstractMoveOn {
    @Override
    public Future<WTransition> moveAsync(final WRequest request, final WTransition wTransition) {
        final ProcessDefinition definition = wTransition.definition();

        if (wTransition.isStarted()) {
            // Error-80604: The wTransition has been started, could not call current divert
            return FnVertx.failOut(_80604Exception409InValidStart.class, definition.getKey());
        }
        return wTransition.start().compose(started -> {
            /*
             * Input `taskId` is null, in this kind of situation the workflow has not been
             * started, the `WMove` is based join `StartEvent`
             *
             * After start() calling, the `move` has the configured get but
             * `ProcessInstance/Task` are both null.
             *
             * Following code should locate the correct `WRule` for parameters, the collection
             * should be
             *
             * [
             *     WRule,
             *     WRule,
             *     WRule,
             *     ...
             * ]
             *
             * Based join configuration the `WRule` must be mutual exclusion
             *
             * Be careful about the definition of `data` of current node:
             *
             * Example:
             * {
             *     "data": {
             *         "draft": "draft"
             *     }
             * }
             */
            final JsonObject parameters = wTransition.moveParameter(request);
            final RunOn runOn = RunOn.get();
            return runOn.startAsync(parameters, wTransition);
        }).compose(wTransition::end);
    }
}
