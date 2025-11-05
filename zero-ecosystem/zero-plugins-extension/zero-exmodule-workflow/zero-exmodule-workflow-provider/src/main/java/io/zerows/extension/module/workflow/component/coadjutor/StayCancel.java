package io.zerows.extension.module.workflow.component.coadjutor;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.workflow.common.em.TodoStatus;
import io.zerows.extension.module.workflow.component.camunda.Io;
import io.zerows.extension.module.workflow.component.camunda.RunOn;
import io.zerows.extension.module.workflow.component.central.AbstractMovement;
import io.zerows.extension.module.workflow.component.toolkit.URequest;
import io.zerows.extension.module.workflow.metadata.WRecord;
import io.zerows.extension.module.workflow.metadata.WRequest;
import io.zerows.extension.module.workflow.metadata.WTransition;
import io.zerows.program.Ux;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class StayCancel extends AbstractMovement implements Stay {
    @Override
    public Future<WRecord> keepAsync(final WRequest request, final WTransition wTransition) {
        /*
         * Instance deleting, but fetch the history and stored into `metadata` field as the final processing
         * Cancel for W_TODO and Camunda
         */
        final ProcessInstance instance = wTransition.instance();
        final Io<Set<String>> ioHistory = Io.ioHistory();
        return ioHistory.run(instance).compose(historySet -> {
            // Cancel data processing
            final JsonObject todoData = URequest.cancelJ(request.request(), wTransition, historySet);
            return this.updateAsync(todoData, wTransition);
        }).compose(record -> {
            // Remove ProcessDefinition
            final RunOn runOn = RunOn.get();
            return runOn.stopAsync(TodoStatus.CANCELED, wTransition)
                .compose(removed -> Ux.future(record));
        }).compose(record -> this.afterAsync(record, wTransition));
    }
}
