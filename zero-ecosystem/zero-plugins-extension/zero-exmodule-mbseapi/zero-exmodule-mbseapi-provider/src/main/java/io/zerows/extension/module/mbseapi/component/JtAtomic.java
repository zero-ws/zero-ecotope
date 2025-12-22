package io.zerows.extension.module.mbseapi.component;

import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.mbseapi.metadata.JtConstant;
import io.zerows.platform.metadata.KRunner;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class JtAtomic {
    /*
     * Monitor for router of each App
     */
    private static final AtomicBoolean AGENT_CONFIG = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOY = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_FAILURE = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOYING = new AtomicBoolean(Boolean.FALSE);
    private static final AtomicBoolean WORKER_DEPLOYED = new AtomicBoolean(Boolean.FALSE);


    public void start(final Logger logger, final JsonObject config) {
        if (!AGENT_CONFIG.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> logger.info("{} Jet 动态路由系统开启，配置 = {}", JtConstant.K_PREFIX_JET, config.encode()), "jet-agent-config");
        }
    }

    public void worker(final Logger logger) {
        if (!WORKER_DEPLOY.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> logger.info("{} 后台发布 Workers……（Async）", JtConstant.K_PREFIX_JET), "jet-worker-deploy");
        }
    }

    public void workerFailure(final Logger logger) {
        if (!WORKER_FAILURE.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> logger.info("{} XHeader 解析导致 Ambient 构造失败", JtConstant.K_PREFIX_JET), "jet-worker-handler");
        }
    }

    public void workerDeploying(final Logger logger, final Integer instances, final String name) {
        if (!WORKER_DEPLOYING.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> logger.info("{} Worker 组件发布，instances = {}, class = {}",
                JtConstant.K_PREFIX_JET, instances, name), "jet-worker-deploying");
        }
    }

    public void workerDeployed(final Logger logger, final Integer instances, final String name) {
        if (!WORKER_DEPLOYED.getAndSet(Boolean.TRUE)) {
            KRunner.run(() -> logger.info("{} Worker `{}` 成功发布！！！( instances = {} )",
                JtConstant.K_PREFIX_JET, name, instances), "jet-worker-deployed");
        }
    }
}
