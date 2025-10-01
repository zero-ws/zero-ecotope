package io.zerows.epoch.corpus.web.security.osgi.service;

import io.zerows.epoch.corpus.web.security.store.ORepositorySecurity;
import io.zerows.epoch.corpus.metadata.zdk.running.ORepository;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-05-01
 */
public class EnergySecureService implements EnergySecure {
    @Override
    public void install(final Bundle bundle) {

        ORepository.ofOr(ORepositorySecurity.class, bundle).whenUpdate(null);

        this.logger().info("The security information has been initialized!! Bundle = {}",
            bundle.getSymbolicName());
    }

    @Override
    public void uninstall(final Bundle bundle) {
        ORepository.ofOr(ORepositorySecurity.class, bundle).whenRemove();


        this.logger().info("Removed the security information!! Bundle = {}",
            bundle.getSymbolicName());
    }
}
