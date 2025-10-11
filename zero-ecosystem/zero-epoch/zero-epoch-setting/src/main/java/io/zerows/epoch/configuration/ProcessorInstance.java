package io.zerows.epoch.configuration;

import io.r2mo.SourceReflect;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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

    private void makeupDeployment(final NodeVertx vertx, final HConfig deployment) {
        if (Objects.nonNull(deployment)) {
            final JsonObject deploymentJ = deployment.options();
            final JsonObject instances = Ut.valueJObject(deploymentJ, "instances");

            // 默认 Agent 选项
            final DeploymentOptions agentOptions = new DeploymentOptions();
            final int agent = Ut.valueInt(instances, "agent");
            if (agent > 0) {
                agentOptions.setInstances(agent);
            } else {
                agentOptions.setInstances(64);
            }
            agentOptions
                .setHa(true)
                .setThreadingModel(ThreadingModel.EVENT_LOOP);
            vertx.agentOptions(agentOptions);
            log.info("[ ZERO ] ( Deployment ) \uD83D\uDFE4 Agent 配置：instances {}, thread = {}, ha = {}",
                agentOptions.getInstances(), agentOptions.getThreadingModel(), agentOptions.isHa());

            // 默认 Worker 选项
            final DeploymentOptions workerOptions = new DeploymentOptions();
            final int worker = Ut.valueInt(instances, "worker");
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
            vertx.workerOptions(workerOptions);
            log.info("[ ZERO ] ( Deployment ) \uD83D\uDFE4 Worker 配置：instances {}, thread = {}, ha = {}",
                workerOptions.getInstances(), workerOptions.getThreadingModel(), workerOptions.isHa());

            // 特殊配置处理
            final JsonObject options = Ut.valueJObject(deploymentJ, KName.OPTIONS);
            options.fieldNames().forEach(clazzName -> {
                final JsonObject optionJ = options.getJsonObject(clazzName);
                final Class<?> deploymentCls = SourceReflect.clazz(clazzName);
                if (Objects.nonNull(deploymentCls)) {
                    final DeploymentOptions clazzOptions = new DeploymentOptions(optionJ);
                    vertx.deploymentOptions(deploymentCls, clazzOptions);
                    log.info("[ ZERO ] \t特殊配置：{} -> instances {}, thread = {}, ha = {}", clazzName,
                        clazzOptions.getInstances(), clazzOptions.getThreadingModel(), clazzOptions.isHa());
                }
            });
        }
    }
}
