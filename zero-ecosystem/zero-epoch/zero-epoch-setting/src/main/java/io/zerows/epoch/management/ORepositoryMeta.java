package io.zerows.epoch.management;

import io.zerows.component.log.OLog;
import io.zerows.epoch.annotations.QaS;
import io.zerows.epoch.assembly.InquirerAgent;
import io.zerows.epoch.assembly.InquirerEvent;
import io.zerows.epoch.assembly.InquirerFilter;
import io.zerows.epoch.assembly.InquirerIpc;
import io.zerows.epoch.assembly.InquirerJoinQaS;
import io.zerows.epoch.assembly.InquirerReceipt;
import io.zerows.epoch.basicore.ActorComponent;
import io.zerows.epoch.basicore.Event;
import io.zerows.epoch.basicore.JointAction;
import io.zerows.epoch.basicore.Receipt;
import io.zerows.epoch.metadata.environment.KSwitcher;
import io.zerows.management.OCacheClass;
import io.zerows.platform.enums.EmAction;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.platform.metadata.KRunner;
import io.zerows.sdk.environment.Inquirer;
import io.zerows.sdk.management.ORepository;
import io.zerows.sdk.osgi.AbstractAmbiguity;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.ncloud.HAeon;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-04-21
 */
public class ORepositoryMeta extends AbstractAmbiguity implements ORepository {

    public ORepositoryMeta(final Bundle bundle) {
        super(bundle);
    }

    /**
     * 全局方法连接点专用
     * <pre><code>
     *     1. {@link QaS}
     * </code></pre>
     */
    @Override
    public void whenStart(final HSetting setting) {
        // 先读取类信息
        final Set<Class<?>> classAll = OCacheClass.entireValue();

        final OLog logger = Ut.Log.boot(ORepositoryMeta.class);

        // 读取全局方法缓存
        final OCacheJoint processor = OCacheJoint.of(this.caller());
        long start = System.currentTimeMillis();
        KRunner.run("meditate-joint",
            // @QaS
            () -> {
                final HAeon aeon = KSwitcher.aeon();
                if (Objects.nonNull(aeon)) {
                    /* Aeon System Enabled */
                    final Inquirer<ConcurrentMap<String, Method>> inquirer = Ut.singleton(InquirerJoinQaS.class);
                    final JointAction action = JointAction.of(EmAction.JoinPoint.QAS);
                    action.put(inquirer.scan(classAll));
                    processor.add(action);
                }
            },
            // @Ipc
            () -> {
                final Inquirer<ConcurrentMap<String, Method>> inquirer = Ut.singleton(InquirerIpc.class);
                final JointAction action = JointAction.of(EmAction.JoinPoint.IPC);
                action.put(inquirer.scan(classAll));
                processor.add(action);
            }
        );
        long end = System.currentTimeMillis();
        logger.info(" {0}ms / Zero Timer: Meditate Method Scanned!",
            end - start);


        start = end;

        final ActorComponent actorComponent = new ActorComponent();
        final Set<Class<?>> classesEndpoint = OCacheClass.entireValue(VertxComponent.ENDPOINT);
        final Set<Class<?>> classQueue = OCacheClass.entireValue(VertxComponent.QUEUE);
        KRunner.run("meditate-core-component",
            // @EndPoint -> Event
            () -> {
                if (!classesEndpoint.isEmpty()) {
                    final Inquirer<Set<Event>> event = Ut.singleton(InquirerEvent.class);
                    final Set<Event> events = event.scan(classesEndpoint);
                    actorComponent.addEvents(events);


                    // 追加到路由管理器中
                    OCacheActor.Tool.addTo(events);
                }
            },
            // @WebFilter -> JSR340
            () -> {
                final Inquirer<ConcurrentMap<String, Set<Event>>> filters = Ut.singleton(InquirerFilter.class);
                actorComponent.addFilters(filters.scan(classAll));
            },
            // @Queue/@QaS -> Receipt
            () -> {
                if (!classQueue.isEmpty()) {
                    final Inquirer<Set<Receipt>> receipt = Ut.singleton(InquirerReceipt.class);
                    actorComponent.addReceipts(receipt.scan(classQueue));
                }
            },
            // Agent Component
            () -> {
                final Inquirer<ConcurrentMap<ServerType, List<Class<?>>>> agent = Ut.singleton(InquirerAgent.class);
                actorComponent.addAgents(agent.scan(classAll));
            }
        );


        final OCacheActor actor = OCacheActor.of(this.caller());
        actor.add(actorComponent);

        end = System.currentTimeMillis();
        logger.info(" {0}ms / Zero Timer: Meditate Core Component Scanned!",
            end - start);
    }
}
