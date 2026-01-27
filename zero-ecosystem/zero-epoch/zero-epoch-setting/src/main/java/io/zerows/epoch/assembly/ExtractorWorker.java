package io.zerows.epoch.assembly;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.DeploymentOptions;
import io.zerows.epoch.jigsaw.NodeStore;
import lombok.extern.slf4j.Slf4j;

/**
 * Worker bottle deployment
 */
@Slf4j
public class ExtractorWorker implements Extractor<DeploymentOptions> {

    public static final String WORKER_HIT = "( Worker ) The worker vertical " +
        "{} will be deployed.";

    private static final Cc<Class<?>, DeploymentOptions> CC_OPTIONS = Cc.open();

    @Override
    public DeploymentOptions extract(final Class<?> clazz) {
        log.info(WORKER_HIT, clazz.getName());


        return CC_OPTIONS.pick(() -> NodeStore.ofDeployment(clazz), clazz);
    }
}
