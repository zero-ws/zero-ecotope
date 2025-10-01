package io.zerows.epoch.osgi.assembly;

import io.zerows.epoch.based.constant.osgi.OMessage;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.metadata.service.ServiceConnector;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * @author lang : 2024-04-17
 */
@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class RuntimeAssemblyActivator extends DependencyActivatorBase {
    @Override
    public void init(final BundleContext context, final DependencyManager dm) throws Exception {
        final Bundle bundle = context.getBundle();


        // Dependency
        final ServiceConnector connector = RuntimeAssemblyDependency.of(bundle);

        connector.serviceDependency(dm, this::createComponent, this::createServiceDependency);
        connector.serviceRegister(dm, this::createComponent);


        // Command Bind
        Ut.Bnd.commandBind(context,
            RuntimeAssemblyCommand.class,
            RuntimeAssemblyCommand.COMMANDS
        );

        Ut.Log.bundle(this.getClass())
            .info(OMessage.Osgi.BUNDLE.START, bundle.getSymbolicName());
    }
}
