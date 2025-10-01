package io.zerows.extension.runtime.workflow.uca.component;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.zerows.epoch.exception.web._60050Exception501NotSupport;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.workflow.atom.runtime.WRecord;
import io.zerows.extension.runtime.workflow.atom.runtime.WRequest;
import io.zerows.extension.runtime.workflow.atom.runtime.WTransition;
import io.zerows.extension.runtime.workflow.eon.WfPool;
import io.zerows.extension.runtime.workflow.exception._80606Exception500EventTypeNull;
import io.zerows.extension.runtime.workflow.exception._80607Exception404RunOnSupplier;
import io.zerows.extension.runtime.workflow.uca.central.Behaviour;
import io.zerows.extension.runtime.workflow.util.Wf;
import io.zerows.unity.Ux;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
interface Pool {
    ConcurrentMap<String, Supplier<MoveOn>> SUPPLIER = new ConcurrentHashMap<>() {
        {
            // UserTask
            this.put(BpmnModelConstants.BPMN_ELEMENT_USER_TASK, MoveOnGenerate::new);
        }
    };
}

public interface MoveOn extends Behaviour {

    static Future<MoveOn> event(final Task task) {
        Objects.requireNonNull(task);
        final String eventType = Wf.nameEvent(task);
        if (Objects.isNull(eventType)) {
            // Error-80606: event type could not be parsed and extracted from task
            return FnVertx.failOut(_80606Exception500EventTypeNull.class, task.getTaskDefinitionKey());
        }

        final Supplier<MoveOn> supplier = Pool.SUPPLIER.getOrDefault(eventType, null);
        if (Objects.isNull(supplier)) {
            // Error-80607: The supplier of event type could not be found.
            return FnVertx.failOut(_80607Exception404RunOnSupplier.class, eventType);
        }
        final MoveOn moveOn = supplier.get();
        LOG.Web.info(MoveOn.class, "MoveOn {0} has been selected, type = {0}",
            moveOn.getClass(), eventType);
        return Ux.future(moveOn);
    }

    static MoveOn instance(final Class<?> moveOnCls) {
        final MoveOn moveOn = WfPool.CC_MOVE_ON.pick(() -> Ut.instance(moveOnCls), moveOnCls.getName());
        LOG.Web.info(MoveOn.class, "MoveOn {0} has been selected", moveOn.getClass());
        return moveOn;
    }

    /*
     *  Event Fire by Programming
     */
    default Future<WRecord> transferAsync(final WRequest request, final WTransition process) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    default Future<WTransition> moveAsync(final WRequest request, final WTransition process) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }
}
