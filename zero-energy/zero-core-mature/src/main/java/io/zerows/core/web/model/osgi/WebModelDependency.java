package io.zerows.core.web.model.osgi;

import io.zerows.core.web.model.extension.HExtension;
import io.zerows.core.web.model.osgi.service.EnergyConfigurationService;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.module.metadata.eon.OMessage;
import io.zerows.module.metadata.osgi.service.EnergyConfiguration;
import io.zerows.module.metadata.zdk.dependency.AbstractConnectorBase;
import io.zerows.module.metadata.zdk.dependency.AbstractConnectorService;
import io.zerows.module.metadata.zdk.service.ServiceConnector;
import io.zerows.module.metadata.zdk.service.ServiceContext;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;

import java.util.function.Supplier;

/**
 * @author lang : 2024-07-01
 */
class WebModelDependency extends AbstractConnectorService {
    private WebModelDependency(final Bundle owner) {
        super(owner);
    }

    static ServiceConnector of(final Bundle owner) {
        return AbstractConnectorBase.of(owner, WebModelDependency::new);
    }

    @Override
    protected ServiceContext withContext(final Bundle owner) {
        final MDConfiguration configuration = HExtension.getOrCreate(owner);
        return ServiceContext.ofModule(configuration);
    }

    @Override
    public void serviceRegister(final DependencyManager dm, final Supplier<Component> supplier) {
        // Enroll / 配置管理服务
        dm.add(supplier.get().setInterface(EnergyConfiguration.class, null)
            .setImplementation(EnergyConfigurationService.class));
        this.logger().info(OMessage.Osgi.SERVICE.REGISTER, EnergyConfiguration.class, EnergyConfigurationService.class);
    }
}
