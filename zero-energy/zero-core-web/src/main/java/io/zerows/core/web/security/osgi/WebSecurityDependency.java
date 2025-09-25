package io.zerows.core.web.security.osgi;

import io.zerows.core.web.security.osgi.service.EnergySecure;
import io.zerows.core.web.security.osgi.service.EnergySecureService;
import io.zerows.module.metadata.eon.OMessage;
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
class WebSecurityDependency extends AbstractConnectorService {
    private WebSecurityDependency(final Bundle bundle) {
        super(bundle);
    }

    static ServiceConnector of(final Bundle bundle) {
        return AbstractConnectorBase.of(bundle, WebSecurityDependency::new);
    }

    @Override
    public void serviceRegister(final DependencyManager dm, final Supplier<Component> supplier) {
        // 元数据管理服务
        dm.add(supplier.get()
            .setInterface(EnergySecure.class, null)
            .setImplementation(EnergySecureService.class)
        );
        this.logger().info(
            OMessage.Osgi.SERVICE.REGISTER,
            EnergySecure.class,
            EnergySecureService.class
        );
    }
}
