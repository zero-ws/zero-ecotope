package io.zerows.extension.module.mbseapi.component;

import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.extension.module.mbseapi.common.JtMsg;
import io.zerows.platform.metadata.KRunner;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.zerows.extension.module.mbseapi.boot.Jt.LOG;

public class JtAtomic {
    /*
     * Monitor for router of each App
     */
    private static final AtomicBoolean AGENT_CONFIG = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOY = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_FAILURE = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOYING = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOYED = new AtomicBoolean(Boolean.FALSE);


    public void start(final LogOf logger, final JsonObject config) {
        if (!AGENT_CONFIG.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Route.info(logger, JtMsg.AGENT_CONFIG, config.encode()), "jet-agent-config");
        }
    }

    public void worker(final LogOf logger) {
        if (!WORKER_DEPLOY.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Worker.info(logger, JtMsg.WORKER_DEPLOY), "jet-worker-deploy");
        }
    }

    public void workerFailure(final LogOf logger) {
        if (!WORKER_FAILURE.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Worker.info(logger, JtMsg.WORKER_FAILURE), "jet-worker-handler");
        }
    }

    public void workerDeploying(final LogOf logger, final Integer instances, final String name) {
        if (!WORKER_DEPLOYING.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Worker.info(logger, JtMsg.WORKER_DEPLOYING,
                String.valueOf(instances), name), "jet-worker-deploying");
        }
    }

    public void workerDeployed(final LogOf logger, final Integer instances, final String name) {
        if (!WORKER_DEPLOYED.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Worker.info(logger, JtMsg.WORKER_DEPLOYED,
                name, String.valueOf(instances)), "jet-worker-deployed");
        }
    }
}
