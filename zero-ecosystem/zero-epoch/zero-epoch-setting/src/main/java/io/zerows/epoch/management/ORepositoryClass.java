package io.zerows.epoch.management;

import io.zerows.epoch.annotations.*;
import io.zerows.epoch.assembly.*;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.platform.metadata.KRunner;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 中间层，针对扫描出现的二次处理过程，不同环境双用模式
 * <pre><code>
 *     1. 全局环境
 *     2. OSGI 环境，会调用此处的服务来执行添加，OSGI 不走底层服务处理
 * </code></pre>
 *
 * @author lang : 2024-04-19
 */
@Slf4j
public class ORepositoryClass extends AbstractAmbiguity implements ORepository {

    public ORepositoryClass(final HBundle bundle) {
        super(bundle);
    }

    /**
     * 全局环境专用方法
     * <pre><code>
     *     1. {@link Actor} - {@link InquirerClassActor}
     *     2. {@link Queue} - {@link InquirerClassQueue}
     *     3. {@link EndPoint} - {@link InquirerClassEndPoint}
     *     4. {@link Worker} - {@link InquirerClassWorker}
     *     5. {@link Agent} - {@link InquirerClassAgent}
     * </code></pre>
     */
    @Override
    public void whenStart(final HSetting setting) {

        this.whenInternal(setting);
    }

    @Override
    public void whenUpdate(final HSetting setting) {

        this.whenInternal(setting);
    }

    private void whenInternal(final HSetting setting) {
        // 读取全局类缓存
        final OCacheClass processor = OCacheClass.of(this.caller());
        final long start = System.currentTimeMillis();
        KRunner.run("meditate-class",
            // AddOn
            () -> {
                final Inquirer<Set<Class<?>>> addOns = Ut.singleton(InquirerClassAddOn.class);
                processor.compile(VertxComponent.ADDON, addOns::scan);
            },
            // @Actor
            () -> {
                final Inquirer<Set<Class<?>>> actors = Ut.singleton(InquirerClassActor.class);
                processor.compile(VertxComponent.ACTOR, actors::scan);
            },
            // @Queue
            () -> {
                final Inquirer<Set<Class<?>>> queues = Ut.singleton(InquirerClassQueue.class);
                processor.compile(VertxComponent.QUEUE, queues::scan);
            },
            // @EndPoint
            () -> {
                final Inquirer<Set<Class<?>>> endPoints = Ut.singleton(InquirerClassEndPoint.class);
                processor.compile(VertxComponent.ENDPOINT, endPoints::scan);
            },
            // Dot / @Worker
            () -> {
                final Inquirer<Set<Class<?>>> worker = Ut.singleton(InquirerClassWorker.class);
                processor.compile(VertxComponent.WORKER, worker::scan);
            },
            // Dot / @Agent
            () -> {
                final Inquirer<Set<Class<?>>> agent = Ut.singleton(InquirerClassAgent.class);
                processor.compile(VertxComponent.AGENT, agent::scan);
            },
            // Dot / @Agent ( type = ServerType )
            () -> {
                final Inquirer<Set<Class<?>>> rpc = Ut.singleton(InquirerClassIpc.class);
                processor.compile(VertxComponent.IPC, rpc::scan);
            },
            // @Validated ( 留待后续扩展 )
            () -> {
                final Inquirer<Set<Class<?>>> validated = Ut.singleton(InquirerClassValidated.class);
                processor.compile(VertxComponent.VALIDATED, validated::scan);
            }
        );
        final long end = System.currentTimeMillis();
        log.info("[ ZERO ] ⏰️ 耗时：{} 毫秒 / 扫描完成！执行器 = {}", end - start, HBundle.id(this.caller(), ORepositoryClass.class));
    }

    @Override
    public void whenRemove() {
        // 缓存类
        final OCacheClass processor = OCacheClass.of(this.caller());
        // 提取移除信息
        final Set<Class<?>> uninstallData = processor.value();
        // 移除
        processor.remove(uninstallData);
    }
}
