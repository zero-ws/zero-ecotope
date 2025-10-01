package io.zerows.core.web.model.store;

import io.zerows.epoch.enums.app.ServerType;
import io.zerows.core.annotations.QaS;
import io.zerows.core.running.context.KRunner;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.atom.Event;
import io.zerows.core.web.model.atom.Receipt;
import io.zerows.core.web.model.atom.action.OActorComponent;
import io.zerows.core.web.model.atom.action.OJointAction;
import io.zerows.core.web.model.eon.em.EmAction;
import io.zerows.core.web.model.uca.scan.*;
import io.zerows.epoch.enums.VertxComponent;
import io.zerows.module.cloud.atom.KSwitcher;
import io.zerows.module.metadata.store.OCacheClass;
import io.zerows.module.metadata.uca.logging.OLog;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import io.zerows.module.metadata.zdk.running.ORepository;
import io.zerows.module.metadata.zdk.uca.Inquirer;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.ncloud.HAeon;
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
                    final OJointAction action = OJointAction.of(EmAction.JoinPoint.QAS);
                    action.put(inquirer.scan(classAll));
                    processor.add(action);
                }
            },
            // @Ipc
            () -> {
                final Inquirer<ConcurrentMap<String, Method>> inquirer = Ut.singleton(InquirerIpc.class);
                final OJointAction action = OJointAction.of(EmAction.JoinPoint.IPC);
                action.put(inquirer.scan(classAll));
                processor.add(action);
            }
        );
        long end = System.currentTimeMillis();
        logger.info(" {0}ms / Zero Timer: Meditate Method Scanned!",
            end - start);


        start = end;

        final OActorComponent actorComponent = new OActorComponent();
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
