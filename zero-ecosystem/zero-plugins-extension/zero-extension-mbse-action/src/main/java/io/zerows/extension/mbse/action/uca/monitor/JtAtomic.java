package io.zerows.extension.mbse.action.uca.monitor;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.context.KRunner;
import io.zerows.epoch.common.log.Annal;
import io.zerows.extension.mbse.action.eon.JtMsg;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.zerows.extension.mbse.action.util.Jt.LOG;

class JtAtomic {
    /*
     * Monitor for router of each App
     */
    private static final AtomicBoolean AGENT_CONFIG = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOY = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_FAILURE = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOYING = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOYED = new AtomicBoolean(Boolean.FALSE);


    void start(final Annal logger, final JsonObject config) {
        if (!AGENT_CONFIG.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Route.info(logger, JtMsg.AGENT_CONFIG, config.encode()), "jet-agent-config");
        }
    }

    void worker(final Annal logger) {
        if (!WORKER_DEPLOY.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Worker.info(logger, JtMsg.WORKER_DEPLOY), "jet-worker-deploy");
        }
    }

    void workerFailure(final Annal logger) {
        if (!WORKER_FAILURE.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Worker.info(logger, JtMsg.WORKER_FAILURE), "jet-worker-handler");
        }
    }

    void workerDeploying(final Annal logger, final Integer instances, final String name) {
        if (!WORKER_DEPLOYING.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Worker.info(logger, JtMsg.WORKER_DEPLOYING,
                String.valueOf(instances), name), "jet-worker-deploying");
        }
    }

    void workerDeployed(final Annal logger, final Integer instances, final String name) {
        if (!WORKER_DEPLOYED.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> LOG.Worker.info(logger, JtMsg.WORKER_DEPLOYED,
                name, String.valueOf(instances)), "jet-worker-deployed");
        }
    }
}
