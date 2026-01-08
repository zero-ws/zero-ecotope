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
        // 1. 基础构造：自动映射 JSON 中的标准字段 (如 instances, ha, worker 等)
        final DeploymentOptions agentOptions = new DeploymentOptions(optionJ);

        // 2. 智能默认值：如果配置文件中没有指定 instances
        if (!optionJ.containsKey("instances")) {
            // 最优策略：EventLoop 类型的实例数建议等于 CPU 核心数
            // 避免过多线程导致上下文切换，也能跑满 CPU
            final int cpuCores = Runtime.getRuntime().availableProcessors();
            agentOptions.setInstances(cpuCores);
        }

        // 3. 强制约束：Agent 必须是 EventLoop 且开启 HA
        agentOptions
            .setHa(true)
            .setThreadingModel(ThreadingModel.EVENT_LOOP);

        log.info("[ ZERO ] ( Deployment ) \uD83D\uDFE4 Agent 配置：instances = {}, thread = {}, ha = {}",
            agentOptions.getInstances(), agentOptions.getThreadingModel(), agentOptions.isHa());
        return agentOptions;
    }

    private DeploymentOptions makeupWorker(final JsonObject optionJ) {
        // 1. 基础构造
        final DeploymentOptions workerOptions = new DeploymentOptions(optionJ);

        // 2. 智能默认值：如果配置文件中没有指定 instances
        if (!optionJ.containsKey("instances")) {
            // Worker 属于阻塞型任务，默认给 64 个并发实例作为兜底
            // 之前的 128 有点过于激进，64 是比较稳健的企业级默认值
            workerOptions.setInstances(64);
        }

        // 3. 动态配置 Worker Pool (关键修复)
        // 优先读取配置中的 poolName，如果没有则生成通用名称
        String poolName = optionJ.getString("workerPoolName");
        if (poolName == null || poolName.isBlank()) {
            poolName = "zero-rachel-momo";
        }

        // 优先读取配置中的 poolSize，如果没有则默认 128
        // 注意：Vert.x 默认是 20，但在微服务/数据库密集型应用中，20 通常不够用，128 是个很好的平衡点
        final int poolSize = optionJ.getInteger("workerPoolSize", 128);

        workerOptions
            .setWorkerPoolName(poolName)
            .setWorkerPoolSize(poolSize)
            .setHa(true)
            .setThreadingModel(ThreadingModel.WORKER);

        log.info("[ ZERO ] ( Deployment ) \uD83D\uDFE4 Worker 配置：pool = {} (size: {}), instances = {}, ha = {}",
            workerOptions.getWorkerPoolName(), workerOptions.getWorkerPoolSize(),
            workerOptions.getInstances(), workerOptions.isHa());
        return workerOptions;
    }
}
