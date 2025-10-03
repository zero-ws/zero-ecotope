package io.zerows.extension.runtime.workflow.uca.deployment;

import io.vertx.core.Future;
import io.zerows.epoch.configuration.MDWorkflow;
import io.zerows.extension.runtime.workflow.eon.WfPool;

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
