package io.zerows.epoch.configuration;

import io.r2mo.SourceReflect;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author lang : 2025-10-10
 */
@Slf4j
public class ProcessorInstance implements Processor<NodeNetwork, ConfigContainer> {
    @Override
    public void makeup(final NodeNetwork network, final ConfigContainer container) {
        container.keyInstance().forEach(vertxName -> {
            // 构造配置信息
            final HConfig instance = container.instance(vertxName);
            if (!(instance instanceof final ConfigInstance configInstance)) {
                throw new _500ServerInternalException("[ ZERO ] 配置数据异常，无法找到实例配置！");
            }

            // 构造 DeploymentOptions
            final NodeVertx nodeVertx = NodeVertx.of(vertxName, network);
            this.makeupDeployment(nodeVertx, container.deployment());
            this.makeupDeployment(nodeVertx, configInstance.deployment());


            // 构造 DeliveryOptions
            this.makeupDelivery(nodeVertx, container.delivery());
            this.makeupDelivery(nodeVertx, configInstance.delivery());


            // 构造 VertxOptions
            final JsonObject optionJ = configInstance.options();
            final VertxOptions vertxOptions = new VertxOptions(optionJ);
            vertxOptions.setPreferNativeTransport(true);
            nodeVertx.vertxOptions(vertxOptions);
            network.add(vertxName, nodeVertx);
        });
    }

    private void makeupDelivery(final NodeVertx vertx, final HConfig delivery) {
        if (Objects.nonNull(delivery)) {
            final JsonObject deliveryJ = delivery.options();
            final DeliveryOptions options = new DeliveryOptions(deliveryJ);
            vertx.deliveryOptions(options);
            log.info("[ ZERO ] ( Delivery   ) \uD83D\uDFE4 配置：timeout {}, codec = {}",
                options.getSendTimeout(), options.getCodecName());
        }
    }

    /**
     * 新版特殊流程，此处的 HConfig 格式为
     * <pre>
     *     - worker
     *     - agent
     *     - workerOf
     *     - agentOf
     * </pre>
     * 刚好上边的结构对应
     * <pre>
     *     - {@link NodeVertx} 中的
     *       - workerOptions / {@link DeploymentOptions}
     *       - agentOptions  / {@link DeploymentOptions}
     *       - name = {@link DeploymentOptions}
     * </pre>
     * 如果实例中有定义则以实例中的为准，默认场景是替换模式，位于 {@link NodeVertx} 内部完成，此处使用的是第二参 true，则是合并模式
     * <pre>
     *     - 1. 默认配置 - 追加模式
     *     - 2. 特殊配置 - 替换模式（针对每一个类）
     * </pre>
     */
    private void makeupDeployment(final NodeVertx vertx, final HConfig deployment) {
        if (Objects.nonNull(deployment)) {
            final JsonObject deploymentJ = deployment.options();

            // 默认 Agent 选项
            final DeploymentOptions agentOptions = this.makeupAgent(Ut.valueJObject(deploymentJ, "agent"));
            vertx.agentOptions(agentOptions, true);

            // 默认 Worker 选项
            final DeploymentOptions workerOptions = this.makeupWorker(Ut.valueJObject(deploymentJ, "worker"));
            vertx.workerOptions(workerOptions, true);

            // 特殊配置 agentOf / workerOf
            final JsonObject agentOf = Ut.valueJObject(deploymentJ, "agentOf");
            this.makeupDeploymentFor(vertx, agentOf, this::makeupAgent);

            final JsonObject workerOf = Ut.valueJObject(deploymentJ, "workerOf");
            this.makeupDeploymentFor(vertx, workerOf, this::makeupWorker);
        }
    }

    private void makeupDeploymentFor(final NodeVertx vertx, final JsonObject config, final Function<JsonObject, DeploymentOptions> buildFn) {
        config.fieldNames().forEach(clazzName -> {
            final Class<?> deploymentCls = SourceReflect.clazz(clazzName);
            if (Objects.nonNull(deploymentCls)) {
                final DeploymentOptions clazzOptions = buildFn.apply(config.getJsonObject(clazzName));
                vertx.deploymentOptions(deploymentCls, clazzOptions);
                log.info("[ ZERO ] \t特殊配置：{} -> instances {}, thread = {}, ha = {}", clazzName,
                    clazzOptions.getInstances(), clazzOptions.getThreadingModel(), clazzOptions.isHa());
            }
        });
    }

    private DeploymentOptions makeupAgent(final JsonObject optionJ) {
        // 默认 Agent 选项
        final DeploymentOptions agentOptions = new DeploymentOptions(optionJ);
        final int agent = Ut.valueInt(optionJ, "instances");
        if (agent > 0) {
            agentOptions.setInstances(agent);
        } else {
            agentOptions.setInstances(64);
        }
        agentOptions
            .setHa(true)
            .setThreadingModel(ThreadingModel.EVENT_LOOP);
        log.info("[ ZERO ] ( Deployment ) \uD83D\uDFE4 Agent 配置：instances {}, thread = {}, ha = {}",
            agentOptions.getInstances(), agentOptions.getThreadingModel(), agentOptions.isHa());
        return agentOptions;
    }

    private DeploymentOptions makeupWorker(final JsonObject optionJ) {
        final DeploymentOptions workerOptions = new DeploymentOptions(optionJ);
        final int worker = Ut.valueInt(optionJ, "instances");
        if (worker > 0) {
            workerOptions.setInstances(worker);
        } else {
            workerOptions.setInstances(128);
        }
        workerOptions
            .setWorkerPoolName("zero-rachel-momo")
            .setWorkerPoolSize(256)
            .setHa(true)
            .setThreadingModel(ThreadingModel.WORKER);
        log.info("[ ZERO ] ( Deployment ) \uD83D\uDFE4 Worker 配置：instances {}, thread = {}, ha = {}",
            workerOptions.getInstances(), workerOptions.getThreadingModel(), workerOptions.isHa());
        return workerOptions;
    }
}
