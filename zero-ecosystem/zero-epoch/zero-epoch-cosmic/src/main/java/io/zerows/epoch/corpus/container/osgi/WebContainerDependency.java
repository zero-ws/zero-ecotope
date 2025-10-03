package io.zerows.epoch.corpus.container.osgi;

import io.zerows.epoch.constant.osgi.OMessage;
import io.zerows.epoch.corpus.container.osgi.service.EnergyDeploymentService;
import io.zerows.epoch.corpus.container.osgi.service.EnergyVertx;
import io.zerows.epoch.corpus.container.osgi.service.EnergyVertxService;
import io.zerows.epoch.corpus.container.osgi.service.provider.InvocationContainer;
import io.zerows.osgi.metadata.service.EnergyDeployment;
import io.zerows.epoch.sdk.osgi.AbstractConnectorBase;
import io.zerows.epoch.sdk.osgi.AbstractConnectorService;
import io.zerows.epoch.sdk.osgi.ServiceConnector;
import io.zerows.epoch.sdk.osgi.ServiceInvocation;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;

import java.util.function.Supplier;

/**
 * @author lang : 2024-05-02
 */
public class WebContainerDependency extends AbstractConnectorService {
    private WebContainerDependency(final Bundle bundle) {
        super(bundle);
    }

    public static ServiceConnector of(final Bundle bundle) {
        return AbstractConnectorBase.of(bundle, WebContainerDependency::new);
    }

    @Override
    public void serviceRegister(final DependencyManager dm, final Supplier<Component> supplier) {
        /*
         * Vertx 实例管理器专用接口（双设计）
         * - 对外直接使用 EnergyVertx
         * - 对内直接使用 StubVertx
         */
        dm.add(supplier.get().setInterface(EnergyVertx.class, null)
            .setImplementation(EnergyVertxService.class));
        this.logger().info(OMessage.Osgi.SERVICE.REGISTER, EnergyVertx.class, EnergyVertxService.class);

        /*
         * Deploy 发布管理专用接口，发布接口在 CONTAINER 和 ASSEMBLY 两个服务中是必须的，因为此处要执行和发布相关的所有
         * 内容，并且在处理过程中，发布管理的完整流程会保证两种服务都可以被调用
         */
        dm.add(supplier.get().setInterface(EnergyDeployment.class, null)
            .setImplementation(EnergyDeploymentService.class)
        );
        this.logger().info(OMessage.Osgi.SERVICE.REGISTER, EnergyDeployment.class, EnergyDeploymentService.class);
    }

    @Override
    protected String[] withConsumers(final Bundle owner) {
        return new String[]{
            ServiceInvocation.ISV.INVOCATION_ASSEMBLY,
        };
    }

    @Override
    protected ServiceInvocation[] withProviders(final Bundle owner) {
        return new ServiceInvocation[]{
            new InvocationContainer(owner),         // 容器服务
        };
    }
}
