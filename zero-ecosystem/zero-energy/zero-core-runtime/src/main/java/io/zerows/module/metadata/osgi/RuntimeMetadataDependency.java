package io.zerows.module.metadata.osgi;

import io.zerows.module.metadata.eon.OMessage;
import io.zerows.module.metadata.osgi.service.EnergyFailure;
import io.zerows.module.metadata.osgi.service.EnergyFailureService;
import io.zerows.module.metadata.osgi.service.EnergyService;
import io.zerows.module.metadata.store.service.EnergyServiceManager;
import io.zerows.module.metadata.zdk.dependency.AbstractConnectorBase;
import io.zerows.module.metadata.zdk.dependency.AbstractConnectorService;
import io.zerows.module.metadata.zdk.service.ServiceConnector;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;

import java.util.function.Supplier;

/**
 * @author lang : 2024-04-22
 */
class RuntimeMetadataDependency extends AbstractConnectorService {
    private RuntimeMetadataDependency(final Bundle bundle) {
        super(bundle);
    }

    static ServiceConnector of(final Bundle bundle) {
        return AbstractConnectorBase.of(bundle, RuntimeMetadataDependency::new);
    }

    @Override
    public void serviceRegister(final DependencyManager dm, final Supplier<Component> supplier) {
        // Enroll / 异常管理服务
        dm.add(supplier.get().setInterface(EnergyFailure.class, null)
            .setImplementation(EnergyFailureService.class));
        this.logger().info(OMessage.Osgi.SERVICE.REGISTER, EnergyFailure.class, EnergyFailureService.class);


        // Enroll / 服务管理
        dm.add(supplier.get().setInterface(EnergyService.class, null)
            .setImplementation(EnergyServiceManager.class));
        this.logger().info(OMessage.Osgi.SERVICE.REGISTER, EnergyService.class, EnergyServiceManager.class);
    }
}
