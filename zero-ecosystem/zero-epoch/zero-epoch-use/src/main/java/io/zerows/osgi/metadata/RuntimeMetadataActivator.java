package io.zerows.osgi.metadata;

import io.zerows.epoch.constant.OMessage;
import io.zerows.epoch.sdk.osgi.ServiceConnector;
import io.zerows.support.Ut;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author lang : 2024-04-17
 */
public class RuntimeMetadataActivator extends DependencyActivatorBase {
    @Override
    public void init(final BundleContext context, final DependencyManager dm) throws Exception {
        final Bundle bundle = context.getBundle();


        // Dependency
        final ServiceConnector connector = RuntimeMetadataDependency.of(bundle);

        connector.serviceRegister(dm, this::createComponent);
        connector.serviceDependency(dm, this::createComponent, this::createServiceDependency);


        // Command Bind
        /*
         * failure  info     all
         * failure  error    all
         *                   size
         *                   {bundleId}
         * service  bundle   all
         *                   {bundleId}
         * cache
         */
        Ut.Bnd.commandBind(context,
            RuntimeMetadataCommand.class,
            RuntimeMetadataCommand.COMMANDS
        );


        Ut.Log.bundle(this.getClass())
            .info(OMessage.Osgi.BUNDLE.START, bundle.getSymbolicName());
    }
}
