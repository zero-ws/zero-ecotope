package io.zerows.extension.module.workflow.metadata;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.module.workflow.component.camunda.Io;
import io.zerows.extension.module.workflow.component.camunda.RunOn;
import io.zerows.extension.module.workflow.component.central.Behaviour;
import io.zerows.extension.module.workflow.component.component.MoveOn;
import io.zerows.extension.module.workflow.component.deployment.DeployOn;
import io.zerows.extension.module.workflow.component.modeling.ActionOn;
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
