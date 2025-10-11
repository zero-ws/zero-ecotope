package io.zerows.epoch.assembly;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.DeploymentOptions;
import io.zerows.epoch.configuration.NodeStore;
import lombok.extern.slf4j.Slf4j;

/**
 * Standard bottle deployment.
 */
@Slf4j
public class ExtractorAgent implements Extractor<DeploymentOptions> {

    public static final String AGENT_HIT = "( Agent ) The standard agent " +
        "{} will be deployed.";
    private static final Cc<Class<?>, DeploymentOptions> CC_OPTIONS = Cc.open();

    @Override
    public DeploymentOptions extract(final Class<?> clazz) {
        log.info(AGENT_HIT, clazz.getName());

        return CC_OPTIONS.pick(() -> NodeStore.ofDeployment(clazz), clazz);
    }
}
