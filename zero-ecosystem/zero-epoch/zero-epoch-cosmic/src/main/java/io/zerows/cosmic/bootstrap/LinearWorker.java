package io.zerows.cosmic.bootstrap;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-10-10
 */
@Slf4j
class LinearWorker extends AbstractAmbiguity implements Linear {
    LinearWorker(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public void start(final Class<?> clazz, final RunVertx runVertx) {
        final DeploymentOptions options = this.getOr(clazz, runVertx);


        if (Objects.isNull(options)) {
            return;
        }


        LinearCenter.startAsync(clazz, options, runVertx);
    }

    @Override
    public void stop(final Class<?> clazz, final RunVertx runVertx) {
        final DeploymentOptions options = this.getOr(clazz, runVertx);


        if (Objects.isNull(options)) {
            return;
        }


        LinearCenter.stopAsync(clazz, options, runVertx);
    }

    private DeploymentOptions getOr(final Class<?> clazz, final RunVertx runVertx) {
        final NodeVertx nodeVertx = runVertx.config();
        final DeploymentOptions options = nodeVertx.deploymentOptions(clazz);
        // 线程不匹配，此处必须是 WORKER
        final ThreadingModel threadingModel = options.getThreadingModel();
        if (ThreadingModel.EVENT_LOOP == threadingModel) {
            log.warn("[ ZERO ] Worker 线程模型不匹配，期望值：{}，实际值：{}",
                String.join(",", ThreadingModel.WORKER.name(), ThreadingModel.VIRTUAL_THREAD.name()), threadingModel);
            return null;
        }
        return options;
    }
}
