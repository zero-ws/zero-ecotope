package io.zerows.epoch.corpus.model.osgi;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.eon.OMessage;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceConnector;
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
