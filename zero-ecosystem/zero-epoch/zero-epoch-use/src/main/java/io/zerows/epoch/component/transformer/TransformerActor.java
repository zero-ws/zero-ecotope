package io.zerows.epoch.component.transformer;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.option.ActorOptions;
import io.zerows.enums.EmDeploy;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.options.Transformer;

/**
 * 原始数据转换器，此数据转换器会带上对应的默认值执行智能化判断和相关操作
 * <pre><code>
 *     1. {@link io.vertx.core.DeploymentOptions} 一个类拥有一个配置
 *     2. {@link io.vertx.core.eventbus.DeliveryOptions} 全局一个配置
 * </code></pre>
 *
 * @author lang : 2024-04-20
 */
public class TransformerActor implements Transformer<ActorOptions> {
    /**
     * 如果编程和配置冲突，则计算 EmDeploy.Mode 来判断
     *
     * @param input 输入的 JsonObject 数据
     *
     * @return {@link ActorOptions} 返回转换后的配置数据
     */
    @Override
    public ActorOptions transform(final JsonObject input) {
        // 初始序列化
        final ActorOptions actorOptions = Ut.deserialize(input, ActorOptions.class);
        /*
         * Deployment 的默认判断条件
         * - @Worker
         * - @Job
         * - @Agent
         * 三种模式判断，对应到 Vert.x 的三种模式
         */
        final EmDeploy.Mode mode = actorOptions.getMode();
        this.logger().info(TransformerMessage.INFO_ROTATE, mode);


        /* DeploymentOptions 初始化，class = DeploymentOptions 计算 */
        final JsonObject options = actorOptions.getOptions();
        options.fieldNames().forEach((className) -> {


            final JsonObject option = Ut.valueJObject(options, className);
            final DeploymentOptions deploymentOptions = new DeploymentOptions(option);


            this.setupWith(deploymentOptions, option);


            this.logger().info(TransformerMessage.INFO_VTC, deploymentOptions.getInstances(), deploymentOptions.isHa(),
                deploymentOptions.toJson().encodePrettily());
            /*
             * 在 ActorOptions 中追加相关信息
             */
            actorOptions.optionDeploy(className, deploymentOptions);
        });


        /* DeliveryOptions 初始化 */
        {
            final JsonObject delivery = actorOptions.getDelivery();
            final DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.setSendTimeout(delivery.getLong("timeout", deliveryOptions.getSendTimeout()));


            this.logger().info(TransformerMessage.INFO_DELIVERY, deliveryOptions.toJson());
            actorOptions.optionDelivery(deliveryOptions);
        }
        return actorOptions;
    }

    private void setupWith(final DeploymentOptions deploymentOptions, final JsonObject options) {
        /* BUG: workerPoolSize */
        if (options.containsKey("workerPoolSize")) {
            final Integer workerPoolSize = options
                .getInteger("workerPoolSize", deploymentOptions.getWorkerPoolSize());
            deploymentOptions.setWorkerPoolSize(workerPoolSize);
            deploymentOptions.setThreadingModel(ThreadingModel.WORKER);

            /* virtual */
            final Boolean multi = options.getBoolean("virtual", Boolean.FALSE);
            if (multi) {
                deploymentOptions.setThreadingModel(ThreadingModel.VIRTUAL_THREAD);
            }
        }
    }
}
