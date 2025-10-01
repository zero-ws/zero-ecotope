package io.zerows.epoch.corpus.domain.osgi;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.eon.OMessage;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author lang : 2024-04-17
 */
public class OBundleInstall extends DependencyActivatorBase {
    @Override
    public void init(final BundleContext context, final DependencyManager dm) throws Exception {

        final Bundle bundle = context.getBundle();


        // Command
        Ut.Log.bundle(this.getClass())
            .info(OMessage.Osgi.BUNDLE.START, bundle.getSymbolicName());
    }
}
