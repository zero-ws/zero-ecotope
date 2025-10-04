package io.zerows.epoch.configuration;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.environment.Processor;
import io.zerows.specification.configuration.HSetting;

/**
 * 通用处理器，调用各个子处理器
 *
 * @author lang : 2024-04-20
 */
public class ProcessorCommon implements Processor<NodeNetwork, HSetting> {

    private static final Cc<String, Processor<NodeNetwork, HSetting>> CC_PROCESSOR = Cc.openThread();
    private final transient Processor<NodeNetwork, HSetting> vertxProcessor;
    private final transient Processor<NodeVertx, HSetting> serverProcessor;
    private final transient Processor<NodeVertx, HSetting> actorProcessor;

    private ProcessorCommon() {
        this.vertxProcessor = new ProcessorVertx();
        this.serverProcessor = new ProcessorServer();
        this.actorProcessor = new ProcessorActor();
    }

    public static Processor<NodeNetwork, HSetting> of() {
        return CC_PROCESSOR.pick(ProcessorCommon::new, ProcessorCommon.class.getName());
    }

    @Override
    public void makeup(final NodeNetwork target, final HSetting setting) {
        // Vertx实例构造处理器
        this.vertxProcessor.makeup(target, setting);

        // 服务器实例构造器，用于构造当前 Vertx（所有实例中的服务器信息）
        target.vertxOptions().forEach((name, nodeVertx) -> {


            // DeploymentOptions / DeliveryOptions
            this.actorProcessor.makeup(nodeVertx, setting);


            // 后期此处可追加过滤规则，现阶段遍历处理即可
            this.serverProcessor.makeup(nodeVertx, setting);


            // 更新 Vertx 实例
            target.add(name, nodeVertx.build());
        });

        // 最后一步，保留了 Post Action 的配置最终处理设置
        target.build(setting);
    }
}
