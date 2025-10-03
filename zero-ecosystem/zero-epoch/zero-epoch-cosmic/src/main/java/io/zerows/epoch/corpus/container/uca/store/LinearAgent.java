package io.zerows.epoch.corpus.container.uca.store;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.sdk.osgi.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-03
 */
class LinearAgent extends AbstractAmbiguity implements StubLinear {
    LinearAgent(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public void runDeploy(final Class<?> clazz, final RunVertx runVertx) {
        final DeploymentOptions options = this.buildOptions(clazz, runVertx);

        if (Objects.isNull(options)) {
            return;
        }

        LinearTool.startAsync(clazz, options, runVertx);


        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> LinearTool.stopAsync(clazz, options, runVertx))
        );
    }

    @Override
    public void runUndeploy(final Class<?> clazz, final RunVertx runVertx) {
        final DeploymentOptions options = this.buildOptions(clazz, runVertx);

        if (Objects.isNull(options)) {
            return;
        }

        LinearTool.stopAsync(clazz, options, runVertx);
    }

    private DeploymentOptions buildOptions(final Class<?> clazz, final RunVertx runVertx) {
        final NodeVertx nodeVertx = runVertx.config();
        final DeploymentOptions options = nodeVertx.optionDeployment(clazz);


        // Verticle Deployment
        final ThreadingModel threadModel = options.getThreadingModel();
        if (ThreadingModel.EVENT_LOOP != threadModel) {
            this.logger().warn(INFO.THREAD_NOT_MATCH, ThreadingModel.EVENT_LOOP.name(), threadModel);
            return null;
        }
        return options;
    }
}
