package io.zerows.osgi.assembly;

import io.zerows.epoch.constant.OMessage;
import io.zerows.sdk.osgi.AbstractConnectorBase;
import io.zerows.sdk.osgi.AbstractConnectorService;
import io.zerows.sdk.osgi.ServiceConnector;
import io.zerows.sdk.osgi.ServiceInvocation;
import io.zerows.osgi.assembly.service.EnergyClass;
import io.zerows.osgi.assembly.service.EnergyClassService;
import io.zerows.osgi.assembly.service.provider.InvocationAssembly;
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
