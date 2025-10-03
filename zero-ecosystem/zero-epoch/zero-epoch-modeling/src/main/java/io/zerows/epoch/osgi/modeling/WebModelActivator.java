package io.zerows.epoch.osgi.modeling;

import io.zerows.epoch.constant.OMessage;
import io.zerows.epoch.sdk.osgi.ServiceConnector;
import io.zerows.support.Ut;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author lang : 2024-07-01
 */
public class WebModelActivator extends DependencyActivatorBase {
    @Override
    public void init(final BundleContext context, final DependencyManager dm) throws Exception {
        final Bundle bundle = context.getBundle();


        // Dependency
        final ServiceConnector connector = WebModelDependency.of(bundle);

        connector.serviceRegister(dm, this::createComponent);
        connector.serviceDependency(dm, this::createComponent, this::createServiceDependency);


        Ut.Log.bundle(this.getClass())
            .info(OMessage.Osgi.BUNDLE.START, bundle.getSymbolicName());
    }
}
