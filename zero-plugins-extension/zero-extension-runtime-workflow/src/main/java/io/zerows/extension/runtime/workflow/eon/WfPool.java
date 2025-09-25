package io.zerows.extension.runtime.workflow.eon;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.runtime.workflow.atom.EngineOn;
import io.zerows.extension.runtime.workflow.uca.camunda.Io;
import io.zerows.extension.runtime.workflow.uca.camunda.RunOn;
import io.zerows.extension.runtime.workflow.uca.central.Behaviour;
import io.zerows.extension.runtime.workflow.uca.component.MoveOn;
import io.zerows.extension.runtime.workflow.uca.deployment.DeployOn;
import io.zerows.extension.runtime.workflow.uca.modeling.ActionOn;
import org.camunda.bpm.engine.repository.ProcessDefinition;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface WfPool {

    @SuppressWarnings("all")
    Cc<String, Io> CC_IO = Cc.openThread();
    Cc<String, ProcessDefinition> CC_DEFINITION = Cc.open();
    Cc<String, RunOn> CC_RUN = Cc.openThread();

    // operation.modeling POOL
    Cc<String, ActionOn> CC_ACTION = Cc.openThread();

    // operation.deployment POOL
    Cc<String, DeployOn> CC_DEPLOY = Cc.open();
    Cc<String, EngineOn> CC_ENGINE = Cc.openThread();

    // operation.component POOL -> Transfer / Movement

    Cc<String, Behaviour> CC_COMPONENT = Cc.openThread();

    // operation.component POOL -> MoveOn
    Cc<String, MoveOn> CC_MOVE_ON = Cc.openThread();
}
