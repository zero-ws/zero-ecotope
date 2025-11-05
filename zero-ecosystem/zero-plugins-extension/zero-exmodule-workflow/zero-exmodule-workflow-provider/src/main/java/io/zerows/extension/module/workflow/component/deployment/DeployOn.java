package io.zerows.extension.module.workflow.component.deployment;

import io.vertx.core.Future;
import io.zerows.epoch.basicore.MDWorkflow;
import io.zerows.extension.module.workflow.metadata.WfPool;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface DeployOn {

    static DeployOn get(final MDWorkflow workflow) {
        return WfPool.CC_DEPLOY.pick(() -> new DeployBpmnService(workflow), workflow.name());
    }

    // Deployment with service
    Future<Boolean> initialize();

    // Bind tentId
    default DeployOn tenant(final String tenantId) {
        return this;
    }
}
