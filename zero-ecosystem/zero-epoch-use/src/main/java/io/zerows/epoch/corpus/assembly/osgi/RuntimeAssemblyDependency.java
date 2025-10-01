package io.zerows.epoch.corpus.assembly.osgi;

import io.zerows.epoch.corpus.assembly.osgi.service.EnergyClass;
import io.zerows.epoch.corpus.assembly.osgi.service.EnergyClassService;
import io.zerows.epoch.corpus.assembly.osgi.service.provider.InvocationAssembly;
import io.zerows.epoch.corpus.metadata.eon.OMessage;
import io.zerows.epoch.corpus.metadata.zdk.dependency.AbstractConnectorBase;
import io.zerows.epoch.corpus.metadata.zdk.dependency.AbstractConnectorService;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceConnector;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceInvocation;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;

import java.util.function.Supplier;

/**
 * @author lang : 2024-04-22
 */
class RuntimeAssemblyDependency extends AbstractConnectorService {
    private RuntimeAssemblyDependency(final Bundle bundle) {
        super(bundle);
    }

    static ServiceConnector of(final Bundle bundle) {
        return AbstractConnectorBase.of(bundle, RuntimeAssemblyDependency::new);
    }

    @Override
    public void serviceRegister(final DependencyManager dm, final Supplier<Component> supplier) {
        // Enroll / 元数据管理服务
        dm.add(supplier.get().setInterface(EnergyClass.class, null)
            .setImplementation(EnergyClassService.class));
        this.logger().info(OMessage.Osgi.SERVICE.REGISTER, EnergyClass.class, EnergyClassService.class);
    }

    @Override
    protected ServiceInvocation[] withProviders(final Bundle provider) {
        return new ServiceInvocation[]{
            new InvocationAssembly(provider) // 扫描组件专用服务
        };
    }
}
