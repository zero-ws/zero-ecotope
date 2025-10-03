package io.zerows.extension.runtime.workflow.uca.camunda;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.constant.VValue;
import io.zerows.epoch.corpus.Ux;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.workflow.bootstrap.WfPin;
import io.zerows.extension.runtime.workflow.exception._80601Exception501EventStartMissing;
import io.zerows.extension.runtime.workflow.exception._80602Exception409EventStartUnique;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class IoEventStart extends AbstractIo<StartEvent> {


    // 「IoBpmn」ProcessDefinition -> List<StartEvent>
    @Override
    public Future<List<StartEvent>> children(final String definitionId) {
        if (Ut.isNil(definitionId)) {
            return Ux.futureL();
        }
        final RepositoryService service = WfPin.camundaRepository();
        final BpmnModelInstance instance = service.getBpmnModelInstance(definitionId);
        final Collection<StartEvent> starts = instance.getModelElementsByType(StartEvent.class);
        if (starts.isEmpty()) {
            return FnVertx.failOut(_80601Exception501EventStartMissing.class, definitionId);
        }
        return Ux.future(new ArrayList<>(starts));
    }


    // 「IoBpmn」ProcessDefinition -> StartEvent
    @Override
    public Future<StartEvent> child(final String definitionId) {
        return this.children(definitionId).compose(list -> {
            final int size = list.size();
            if (VValue.ONE == size) {
                return Ux.future(list.get(VValue.IDX));
            } else {
                return FnVertx.failOut(_80602Exception409EventStartUnique.class, size, definitionId);
            }
        });
    }

    @Override
    public Future<JsonObject> out(final JsonObject workflow, final List<StartEvent> starts) {
        if (1 == starts.size()) {
            final StartEvent event = starts.get(VValue.IDX);
            /*
             * task:        id
             * taskName:    name
             */
            workflow.put(KName.Flow.TASK, event.getId());
            workflow.put(KName.Flow.TASK_NAME, event.getName());
        } else {
            final JsonObject taskMap = new JsonObject();
            starts.forEach(start -> taskMap.put(start.getId(), start.getName()));
            /*
             * id1:      name1
             * id2:      name2
             */
            workflow.put(KName.Flow.TASK, taskMap);
        }
        return Ux.future(workflow);
    }
}
