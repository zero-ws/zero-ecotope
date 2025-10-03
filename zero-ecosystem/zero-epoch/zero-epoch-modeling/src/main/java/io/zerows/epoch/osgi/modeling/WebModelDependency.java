package io.zerows.epoch.osgi.modeling;

import io.zerows.epoch.constant.OMessage;
import io.zerows.epoch.configuration.MDConfiguration;
import io.zerows.epoch.corpus.extension.HExtension;
import io.zerows.osgi.metadata.service.EnergyConfiguration;
import io.zerows.epoch.osgi.modeling.service.EnergyConfigurationService;
import io.zerows.sdk.osgi.AbstractConnectorBase;
import io.zerows.sdk.osgi.AbstractConnectorService;
import io.zerows.sdk.osgi.ServiceConnector;
import io.zerows.sdk.osgi.ServiceContext;
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
