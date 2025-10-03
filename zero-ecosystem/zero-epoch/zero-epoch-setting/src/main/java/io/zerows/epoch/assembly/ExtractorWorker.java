package io.zerows.epoch.assembly;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.DeploymentOptions;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.management.OCacheNode;
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

        final NodeNetwork network = OCacheNode.of().network();
        final NodeVertx nodeVertx = network.get();

        return CC_OPTIONS.pick(() -> nodeVertx.optionDeployment(clazz), clazz);
        // FnZero.po?l(OPTIONS, clazz, () -> rotate.spinWorker(clazz));
    }
}
