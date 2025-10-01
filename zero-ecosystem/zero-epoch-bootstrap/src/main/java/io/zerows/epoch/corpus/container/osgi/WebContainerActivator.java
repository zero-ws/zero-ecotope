package io.zerows.epoch.corpus.container.osgi;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.eon.OMessage;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceConnector;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * @author lang : 2024-05-02
 */
@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class WebContainerActivator extends DependencyActivatorBase {
    @Override
    public void init(final BundleContext context, final DependencyManager dm) throws Exception {
        final Bundle bundle = context.getBundle();


        // Dependency
        final ServiceConnector connector = WebContainerDependency.of(bundle);

        connector.serviceDependency(dm, this::createComponent, this::createServiceDependency);
        connector.serviceRegister(dm, this::createComponent);


        // Command Bind
        Ut.Bnd.commandBind(context,
            WebContainerCommand.class,
            WebContainerCommand.COMMANDS
        );

        Ut.Log.bundle(this.getClass())
            .info(OMessage.Osgi.BUNDLE.START, bundle.getSymbolicName());
    }
}
