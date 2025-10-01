package io.zerows.epoch.corpus.configuration.osgi;

import io.zerows.epoch.corpus.configuration.osgi.service.EnergyOption;
import io.zerows.epoch.corpus.configuration.osgi.service.EnergyOptionService;
import io.zerows.epoch.corpus.metadata.eon.OMessage;
import io.zerows.epoch.corpus.metadata.zdk.dependency.AbstractConnectorBase;
import io.zerows.epoch.corpus.metadata.zdk.dependency.AbstractConnectorService;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceConnector;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;

import java.util.function.Supplier;

/**
 * @author lang : 2024-04-22
 */
class RuntimeConfigurationDependency extends AbstractConnectorService {
    private RuntimeConfigurationDependency(final Bundle bundle) {
        super(bundle);
    }

    static ServiceConnector of(final Bundle bundle) {
        return AbstractConnectorBase.of(bundle, RuntimeConfigurationDependency::new);
    }

    @Override
    public void serviceRegister(final DependencyManager dm, final Supplier<Component> supplier) {
        // 配置管理服务
        dm.add(supplier.get()
            .setInterface(EnergyOption.class, null)
            .setImplementation(EnergyOptionService.class)
        );
        this.logger().info(OMessage.Osgi.SERVICE.REGISTER,
            EnergyOption.class,
            EnergyOptionService.class
        );
    }
}
