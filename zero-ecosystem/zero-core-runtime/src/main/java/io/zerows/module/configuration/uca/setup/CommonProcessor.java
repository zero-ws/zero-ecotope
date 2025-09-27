package io.zerows.module.configuration.uca.setup;

import io.r2mo.typed.cc.Cc;
import io.zerows.module.configuration.atom.NodeNetwork;
import io.zerows.module.configuration.atom.NodeVertx;
import io.zerows.module.configuration.zdk.Processor;
import io.zerows.specification.configuration.HSetting;

/**
 * 通用处理器，调用各个子处理器
 *
 * @author lang : 2024-04-20
 */
public class CommonProcessor implements Processor<NodeNetwork, HSetting> {

    private static final Cc<String, Processor<NodeNetwork, HSetting>> CC_PROCESSOR = Cc.openThread();
    private final transient Processor<NodeNetwork, HSetting> vertxProcessor;
    private final transient Processor<NodeVertx, HSetting> serverProcessor;
    private final transient Processor<NodeVertx, HSetting> actorProcessor;

    private CommonProcessor() {
        this.vertxProcessor = new VertxProcessor();
        this.serverProcessor = new ServerProcessor();
        this.actorProcessor = new ActorProcessor();
    }

    public static Processor<NodeNetwork, HSetting> of() {
        return CC_PROCESSOR.pick(CommonProcessor::new, CommonProcessor.class.getName());
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
