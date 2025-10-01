package io.zerows.core.web.model.uca.extract;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.DeploymentOptions;
import io.zerows.epoch.common.uca.log.Annal;
import io.zerows.module.configuration.atom.NodeNetwork;
import io.zerows.module.configuration.atom.NodeVertx;
import io.zerows.module.configuration.store.OCacheNode;

/**
 * Worker bottle deployment
 */
public class ExtractorWorker implements Extractor<DeploymentOptions> {

    private static final Annal LOGGER = Annal.get(ExtractorWorker.class);

    private static final Cc<Class<?>, DeploymentOptions> CC_OPTIONS = Cc.open();

    @Override
    public DeploymentOptions extract(final Class<?> clazz) {
        LOGGER.info(INFO.WORKER_HIT, clazz.getName());

        final NodeNetwork network = OCacheNode.of().network();
        final NodeVertx nodeVertx = network.get();

        return CC_OPTIONS.pick(() -> nodeVertx.optionDeployment(clazz), clazz);
        // FnZero.po?l(OPTIONS, clazz, () -> rotate.spinWorker(clazz));
    }
}
